package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.AsignaturaDTO;
import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.exception.DatoDuplicadoException;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.exception.OperacionNoPermitidaException;
import co.edu.unbosque.gestorhorarios.exception.RecursoNoEncontradoException;
import co.edu.unbosque.gestorhorarios.repository.AsignaturaRepository;
import co.edu.unbosque.gestorhorarios.repository.DocenteAsignaturaRepository;
import co.edu.unbosque.gestorhorarios.repository.GrupoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AsignaturaService implements CRUDOperation<AsignaturaDTO> {

    private static final int LONGITUD_MAXIMA_CODIGO = 20;
    private static final int LONGITUD_MAXIMA_NOMBRE = 120;
    private static final int MINIMO_SESIONES_SEMANA = 1;
    private static final int MAXIMO_SESIONES_SEMANA = 4;

    private final AsignaturaRepository asignaturaRepository;
    private final GrupoRepository grupoRepository;
    private final DocenteAsignaturaRepository docenteAsignaturaRepository;
    private final ProteccionHorarioAprobadoService proteccionHorarioAprobado;

    public AsignaturaService(AsignaturaRepository asignaturaRepository, GrupoRepository grupoRepository,
            DocenteAsignaturaRepository docenteAsignaturaRepository,
            ProteccionHorarioAprobadoService proteccionHorarioAprobado) {
        this.asignaturaRepository = asignaturaRepository;
        this.grupoRepository = grupoRepository;
        this.docenteAsignaturaRepository = docenteAsignaturaRepository;
        this.proteccionHorarioAprobado = proteccionHorarioAprobado;
    }

    @Override
    public int create(AsignaturaDTO dto) {
        validarCampos(dto);
        if (asignaturaRepository.existsByCodigoIgnoreCase(dto.getCodigo().trim())) {
            throw new DatoDuplicadoException("Ya existe una asignatura con el código " + dto.getCodigo().trim());
        }
        Asignatura asignatura = new Asignatura();
        copiarCampos(dto, asignatura);
        asignaturaRepository.save(asignatura);
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignaturaDTO> getAll() {
        return asignaturaRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    @Transactional(readOnly = true)
    public AsignaturaDTO getById(Long id) {
        return convertirADTO(buscar(id));
    }

    @Override
    public int deleteById(Long id) {
        Asignatura asignatura = buscar(id);
        if (grupoRepository.existsByAsignaturaId(id)) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar la asignatura porque tiene grupos asociados");
        }
        docenteAsignaturaRepository.deleteByAsignaturaId(id);
        asignaturaRepository.delete(asignatura);
        return 0;
    }

    @Override
    public int updateById(Long id, AsignaturaDTO dto) {
        Asignatura asignatura = buscar(id);
        validarCampos(dto);
        if (asignaturaRepository.existsByCodigoIgnoreCaseAndIdNot(dto.getCodigo().trim(), id)) {
            throw new DatoDuplicadoException("Ya existe una asignatura con el código " + dto.getCodigo().trim());
        }
        if (!asignatura.getSesionesSemana().equals(dto.getSesionesSemana())
                && grupoRepository.existsByAsignaturaId(id)) {
            throw new OperacionNoPermitidaException(
                    "No se puede cambiar el número de sesiones por semana porque la asignatura tiene grupos asociados");
        }
        proteccionHorarioAprobado.aplicarSinDanarHorariosAprobados(() -> {
            copiarCampos(dto, asignatura);
            asignaturaRepository.save(asignatura);
        });
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return asignaturaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exist(Long id) {
        return asignaturaRepository.existsById(id);
    }

    public AsignaturaDTO convertirADTO(Asignatura asignatura) {
        return new AsignaturaDTO(asignatura.getId(), asignatura.getCodigo(), asignatura.getNombre(),
                asignatura.getSesionesSemana(), asignatura.getTipoAulaRequerido());
    }

    private Asignatura buscar(Long id) {
        return asignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la asignatura con id " + id));
    }

    private void validarCampos(AsignaturaDTO dto) {
        if (dto == null) {
            throw new DatoInvalidoException("Los datos de la asignatura son obligatorios");
        }
        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new DatoInvalidoException("El código de la asignatura es obligatorio");
        }
        if (dto.getCodigo().trim().length() > LONGITUD_MAXIMA_CODIGO) {
            throw new DatoInvalidoException(
                    "El código de la asignatura admite hasta " + LONGITUD_MAXIMA_CODIGO + " caracteres");
        }
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new DatoInvalidoException("El nombre de la asignatura es obligatorio");
        }
        if (dto.getNombre().trim().length() > LONGITUD_MAXIMA_NOMBRE) {
            throw new DatoInvalidoException(
                    "El nombre de la asignatura admite hasta " + LONGITUD_MAXIMA_NOMBRE + " caracteres");
        }
        if (dto.getSesionesSemana() == null) {
            throw new DatoInvalidoException("El número de sesiones por semana es obligatorio");
        }
        if (dto.getSesionesSemana() < MINIMO_SESIONES_SEMANA || dto.getSesionesSemana() > MAXIMO_SESIONES_SEMANA) {
            throw new DatoInvalidoException("El número de sesiones por semana debe estar entre "
                    + MINIMO_SESIONES_SEMANA + " y " + MAXIMO_SESIONES_SEMANA);
        }
        if (dto.getTipoAulaRequerido() == null) {
            throw new DatoInvalidoException("El tipo de aula requerido es obligatorio");
        }
    }

    private void copiarCampos(AsignaturaDTO dto, Asignatura asignatura) {
        asignatura.setCodigo(dto.getCodigo().trim());
        asignatura.setNombre(dto.getNombre().trim());
        asignatura.setSesionesSemana(dto.getSesionesSemana());
        asignatura.setTipoAulaRequerido(dto.getTipoAulaRequerido());
    }
}
