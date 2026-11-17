package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.GrupoDTO;
import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.EstadoHorario;
import co.edu.unbosque.gestorhorarios.exception.DatoDuplicadoException;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.exception.IncidenciasHorarioException;
import co.edu.unbosque.gestorhorarios.exception.OperacionNoPermitidaException;
import co.edu.unbosque.gestorhorarios.exception.RecursoNoEncontradoException;
import co.edu.unbosque.gestorhorarios.repository.AsignaturaRepository;
import co.edu.unbosque.gestorhorarios.repository.DocenteRepository;
import co.edu.unbosque.gestorhorarios.repository.GrupoRepository;
import co.edu.unbosque.gestorhorarios.repository.HorarioRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GrupoService implements CRUDOperation<GrupoDTO> {

    private final GrupoRepository grupoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final HorarioRepository horarioRepository;
    private final DocenteRepository docenteRepository;
    private final SesionClaseService sesionClaseService;
    private final ValidacionService validacionService;

    public GrupoService(GrupoRepository grupoRepository, AsignaturaRepository asignaturaRepository,
            HorarioRepository horarioRepository, DocenteRepository docenteRepository,
            SesionClaseService sesionClaseService, ValidacionService validacionService) {
        this.grupoRepository = grupoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.horarioRepository = horarioRepository;
        this.docenteRepository = docenteRepository;
        this.sesionClaseService = sesionClaseService;
        this.validacionService = validacionService;
    }

    @Override
    public int create(GrupoDTO dto) {
        validarCampos(dto);
        Asignatura asignatura = asignaturaRepository.findById(dto.getAsignaturaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la asignatura con id " + dto.getAsignaturaId()));
        Horario horario = horarioRepository.findById(dto.getHorarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el horario con id " + dto.getHorarioId()));
        exigirBorrador(horario);
        if (grupoRepository.existsByHorarioIdAndAsignaturaIdAndNumero(horario.getId(), asignatura.getId(),
                dto.getNumero())) {
            throw new DatoDuplicadoException("Ya existe el grupo " + dto.getNumero() + " de la asignatura "
                    + asignatura.getCodigo() + " en el horario " + horario.getSemestre());
        }
        Grupo grupo = new Grupo();
        grupo.setNumero(dto.getNumero());
        grupo.setCupo(dto.getCupo());
        grupo.setAsignatura(asignatura);
        grupo.setHorario(horario);
        grupo.setDocente(buscarDocente(dto.getDocenteId()));
        for (int sesion = 0; sesion < asignatura.getSesionesSemana(); sesion++) {
            grupo.getSesiones().add(new SesionClase(grupo));
        }
        grupoRepository.saveAndFlush(grupo);
        rechazarSiIncumpleReglas(grupo);
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrupoDTO> getAll() {
        return ordenarYConvertir(grupoRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<GrupoDTO> getByHorarioId(Long horarioId) {
        if (!horarioRepository.existsById(horarioId)) {
            throw new RecursoNoEncontradoException("No existe el horario con id " + horarioId);
        }
        return ordenarYConvertir(grupoRepository.findByHorarioId(horarioId));
    }

    @Transactional(readOnly = true)
    public GrupoDTO getById(Long id) {
        return convertirADTO(buscar(id));
    }

    @Override
    public int deleteById(Long id) {
        Grupo grupo = buscar(id);
        exigirBorrador(grupo.getHorario());
        grupoRepository.delete(grupo);
        return 0;
    }

    @Override
    public int updateById(Long id, GrupoDTO dto) {
        Grupo grupo = buscar(id);
        exigirBorrador(grupo.getHorario());
        validarCampos(dto);
        if (!dto.getAsignaturaId().equals(grupo.getAsignatura().getId())
                || !dto.getHorarioId().equals(grupo.getHorario().getId())) {
            throw new DatoInvalidoException("No se puede cambiar la asignatura ni el horario de un grupo registrado");
        }
        if (grupoRepository.existsByHorarioIdAndAsignaturaIdAndNumeroAndIdNot(grupo.getHorario().getId(),
                grupo.getAsignatura().getId(), dto.getNumero(), id)) {
            throw new DatoDuplicadoException("Ya existe el grupo " + dto.getNumero() + " de la asignatura "
                    + grupo.getAsignatura().getCodigo() + " en el horario " + grupo.getHorario().getSemestre());
        }
        Docente docenteNuevo = buscarDocente(dto.getDocenteId());
        Long docenteAnteriorId = grupo.getDocente() == null ? null : grupo.getDocente().getId();
        grupo.setNumero(dto.getNumero());
        grupo.setCupo(dto.getCupo());
        grupo.setDocente(docenteNuevo);
        for (SesionClase sesion : grupo.getSesiones()) {
            sesion.setDocente(docenteNuevo);
        }
        rechazarSiIncumpleReglas(grupo);
        grupoRepository.save(grupo);
        if (!Objects.equals(docenteAnteriorId, dto.getDocenteId())) {
            sesionClaseService.guardarUbicaciones();
        }
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return grupoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exist(Long id) {
        return grupoRepository.existsById(id);
    }

    private Grupo buscar(Long id) {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el grupo con id " + id));
    }

    private Docente buscarDocente(Long docenteId) {
        if (docenteId == null) {
            return null;
        }
        return docenteRepository.findById(docenteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el docente con id " + docenteId));
    }

    private void exigirBorrador(Horario horario) {
        if (horario.getEstado() == EstadoHorario.APROBADO) {
            throw new OperacionNoPermitidaException("El horario " + horario.getSemestre()
                    + " está aprobado y no admite cambios hasta devolverlo a borrador");
        }
    }

    private void validarCampos(GrupoDTO dto) {
        if (dto == null) {
            throw new DatoInvalidoException("Los datos del grupo son obligatorios");
        }
        if (dto.getAsignaturaId() == null) {
            throw new DatoInvalidoException("La asignatura del grupo es obligatoria");
        }
        if (dto.getHorarioId() == null) {
            throw new DatoInvalidoException("El horario del grupo es obligatorio");
        }
        if (dto.getNumero() == null || dto.getNumero() <= 0) {
            throw new DatoInvalidoException("El número del grupo debe ser un entero positivo");
        }
        if (dto.getCupo() == null || dto.getCupo() <= 0) {
            throw new DatoInvalidoException("El cupo del grupo debe ser un entero positivo");
        }
    }

    private void rechazarSiIncumpleReglas(Grupo grupo) {
        List<Long> sesionIds = grupo.getSesiones().stream().map(SesionClase::getId).toList();
        List<IncidenciaDTO> errores = validacionService
                .filtrarErrores(validacionService.validarSesiones(grupo.getHorario(), sesionIds));
        if (!errores.isEmpty()) {
            throw new IncidenciasHorarioException("El grupo se rechazó porque incumple las reglas del horario",
                    errores);
        }
    }

    private List<GrupoDTO> ordenarYConvertir(List<Grupo> grupos) {
        return grupos.stream()
                .sorted(Comparator.comparing((Grupo grupo) -> grupo.getHorario().getSemestre())
                        .thenComparing(grupo -> grupo.getAsignatura().getCodigo())
                        .thenComparing(Grupo::getNumero))
                .map(this::convertirADTO)
                .toList();
    }

    private GrupoDTO convertirADTO(Grupo grupo) {
        return new GrupoDTO(grupo.getId(), grupo.getNumero(), grupo.getCupo(), grupo.getAsignatura().getId(),
                grupo.getHorario().getId(), grupo.getDocente() == null ? null : grupo.getDocente().getId());
    }
}
