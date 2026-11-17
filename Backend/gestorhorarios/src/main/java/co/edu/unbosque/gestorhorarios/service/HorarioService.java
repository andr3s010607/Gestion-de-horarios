package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.CuadriculaHorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.HorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.dto.MovimientoSesionDTO;
import co.edu.unbosque.gestorhorarios.dto.ResultadoGeneracionHorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.ResultadoMovimientoDTO;
import co.edu.unbosque.gestorhorarios.dto.SesionClaseDTO;
import co.edu.unbosque.gestorhorarios.dto.SesionSinUbicarDTO;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.EstadoHorario;
import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import co.edu.unbosque.gestorhorarios.exception.DatoDuplicadoException;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.exception.IncidenciasHorarioException;
import co.edu.unbosque.gestorhorarios.exception.OperacionNoPermitidaException;
import co.edu.unbosque.gestorhorarios.exception.RecursoNoEncontradoException;
import co.edu.unbosque.gestorhorarios.repository.AsignaturaRepository;
import co.edu.unbosque.gestorhorarios.repository.AulaRepository;
import co.edu.unbosque.gestorhorarios.repository.DocenteRepository;
import co.edu.unbosque.gestorhorarios.repository.GrupoRepository;
import co.edu.unbosque.gestorhorarios.repository.HorarioRepository;
import co.edu.unbosque.gestorhorarios.repository.SesionClaseRepository;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HorarioService implements CRUDOperation<HorarioDTO> {

    private static final Pattern FORMATO_SEMESTRE = Pattern.compile("\\d{4}-[12]");
    private static final String RESTRICCION_OCUPACION_DOCENTE = "uq_ocupacion_docente";

    private final HorarioRepository horarioRepository;
    private final GrupoRepository grupoRepository;
    private final SesionClaseRepository sesionClaseRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final DocenteRepository docenteRepository;
    private final AulaRepository aulaRepository;
    private final SesionClaseService sesionClaseService;
    private final ValidacionService validacionService;
    private final GeneradorHorario generadorHorario;
    private final ReglaHorarioService reglaHorarioService;

    public HorarioService(HorarioRepository horarioRepository, GrupoRepository grupoRepository,
            SesionClaseRepository sesionClaseRepository, AsignaturaRepository asignaturaRepository,
            DocenteRepository docenteRepository, AulaRepository aulaRepository,
            SesionClaseService sesionClaseService, ValidacionService validacionService,
            GeneradorHorario generadorHorario, ReglaHorarioService reglaHorarioService) {
        this.horarioRepository = horarioRepository;
        this.grupoRepository = grupoRepository;
        this.sesionClaseRepository = sesionClaseRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.docenteRepository = docenteRepository;
        this.aulaRepository = aulaRepository;
        this.sesionClaseService = sesionClaseService;
        this.validacionService = validacionService;
        this.generadorHorario = generadorHorario;
        this.reglaHorarioService = reglaHorarioService;
    }

    @Override
    public int create(HorarioDTO dto) {
        String semestre = validarSemestre(dto);
        if (horarioRepository.existsBySemestre(semestre)) {
            throw new DatoDuplicadoException("Ya existe un horario para el semestre " + semestre);
        }
        Horario horario = new Horario();
        horario.setSemestre(semestre);
        horario.setEstado(EstadoHorario.BORRADOR);
        horarioRepository.save(horario);
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioDTO> getAll() {
        return horarioRepository.findAll().stream()
                .sorted(Comparator.comparing(Horario::getSemestre))
                .map(this::convertirADTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public HorarioDTO getById(Long id) {
        return convertirADTO(buscar(id));
    }

    @Override
    public int deleteById(Long id) {
        Horario horario = buscar(id);
        exigirBorrador(horario);
        grupoRepository.deleteAll(grupoRepository.findByHorarioId(id));
        horarioRepository.delete(horario);
        return 0;
    }

    @Override
    public int updateById(Long id, HorarioDTO dto) {
        Horario horario = buscar(id);
        exigirBorrador(horario);
        String semestre = validarSemestre(dto);
        if (horarioRepository.existsBySemestreAndIdNot(semestre, id)) {
            throw new DatoDuplicadoException("Ya existe un horario para el semestre " + semestre);
        }
        horario.setSemestre(semestre);
        horarioRepository.save(horario);
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return horarioRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exist(Long id) {
        return horarioRepository.existsById(id);
    }

    public ResultadoGeneracionHorarioDTO generarPropuesta(Long horarioId) {
        Horario horario = buscar(horarioId);
        exigirBorrador(horario);
        exigirDatosMinimos(horario);
        Map<SesionClase, IncidenciaDTO> causasSinUbicar = generadorHorario.ubicarSesionesPendientes(horario,
                aulaRepository.findAllByOrderByCodigoAsc());
        sesionClaseService.guardarUbicaciones();
        List<SesionClase> sesiones = horario.getGrupos().stream()
                .flatMap(grupo -> grupo.getSesiones().stream())
                .toList();
        List<SesionClaseDTO> ubicadas = sesionClaseService
                .ordenarYConvertir(sesiones.stream().filter(SesionClase::estaUbicada).toList());
        List<SesionSinUbicarDTO> sinUbicar = new ArrayList<>();
        for (SesionClase sesion : sesiones) {
            if (!sesion.estaUbicada()) {
                IncidenciaDTO causa = causasSinUbicar.get(sesion);
                sinUbicar.add(new SesionSinUbicarDTO(sesionClaseService.convertirADTO(sesion), causa.getCodigo(),
                        causa.getMensaje()));
            }
        }
        List<IncidenciaDTO> advertencias = validacionService.filtrarAdvertencias(validacionService.validar(horario));
        return new ResultadoGeneracionHorarioDTO(ubicadas, sinUbicar, advertencias);
    }

    @Transactional(readOnly = true)
    public List<IncidenciaDTO> validarHorario(Long horarioId) {
        return validacionService.validar(buscar(horarioId));
    }

    @Transactional(readOnly = true)
    public CuadriculaHorarioDTO obtenerCuadricula(Long horarioId) {
        Horario horario = buscar(horarioId);
        return new CuadriculaHorarioDTO(convertirADTO(horario), Arrays.asList(DiaSemana.values()),
                convertirAHoras(reglaHorarioService.minutosInicioValidos(DiaSemana.LUNES.getNumero())),
                convertirAHoras(reglaHorarioService.minutosInicioValidos(DiaSemana.SABADO.getNumero())),
                sesionClaseService.ordenarYConvertir(sesionClaseRepository.findByHorarioIdAndDiaIsNotNull(horarioId)),
                sesionClaseService.ordenarYConvertir(sesionClaseRepository.findByHorarioIdAndDiaIsNull(horarioId)));
    }

    @Transactional(readOnly = true)
    public List<SesionClaseDTO> getSesiones(Long horarioId) {
        buscar(horarioId);
        return sesionClaseService.ordenarYConvertir(sesionClaseRepository.findByHorarioId(horarioId));
    }

    @Transactional(readOnly = true)
    public List<SesionClaseDTO> consultarHorarioCompleto(Long horarioId) {
        buscar(horarioId);
        return sesionClaseService.ordenarYConvertir(sesionClaseRepository.findByHorarioIdAndDiaIsNotNull(horarioId));
    }

    @Transactional(readOnly = true)
    public List<SesionClaseDTO> consultarPorDocente(Long horarioId, Long docenteId) {
        buscar(horarioId);
        if (!docenteRepository.existsById(docenteId)) {
            throw new RecursoNoEncontradoException("No existe el docente con id " + docenteId);
        }
        return sesionClaseService.ordenarYConvertir(
                sesionClaseRepository.findByHorarioIdAndGrupoDocenteIdAndDiaIsNotNull(horarioId, docenteId));
    }

    @Transactional(readOnly = true)
    public List<SesionClaseDTO> consultarPorAula(Long horarioId, Long aulaId) {
        buscar(horarioId);
        if (!aulaRepository.existsById(aulaId)) {
            throw new RecursoNoEncontradoException("No existe el aula con id " + aulaId);
        }
        return sesionClaseService.ordenarYConvertir(
                sesionClaseRepository.findByHorarioIdAndAulaIdAndDiaIsNotNull(horarioId, aulaId));
    }

    @Transactional(readOnly = true)
    public List<SesionClaseDTO> consultarPorAsignatura(Long horarioId, Long asignaturaId) {
        buscar(horarioId);
        if (!asignaturaRepository.existsById(asignaturaId)) {
            throw new RecursoNoEncontradoException("No existe la asignatura con id " + asignaturaId);
        }
        return sesionClaseService.ordenarYConvertir(
                sesionClaseRepository.findByHorarioIdAndGrupoAsignaturaIdAndDiaIsNotNull(horarioId, asignaturaId));
    }

    public ResultadoMovimientoDTO moverSesion(Long sesionId, MovimientoSesionDTO movimiento) {
        SesionClase sesion = sesionClaseService.buscar(sesionId);
        Horario horario = sesion.getHorario();
        exigirBorrador(horario);
        if (movimiento == null || movimiento.getDia() == null || movimiento.getHoraInicio() == null
                || movimiento.getAulaId() == null) {
            throw new DatoInvalidoException("El día, la hora de inicio y el aula de destino son obligatorios");
        }
        Aula aula = aulaRepository.findById(movimiento.getAulaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el aula con id " + movimiento.getAulaId()));
        int minutoInicio = ReglaHorarioService.convertirAMinutos(movimiento.getHoraInicio());
        sesion.ubicar(movimiento.getDia().getNumero(), minutoInicio,
                minutoInicio + reglaHorarioService.duracionSesionMinutos(), aula);
        List<IncidenciaDTO> incidencias = validacionService.validarSesiones(horario, List.of(sesionId));
        List<IncidenciaDTO> errores = validacionService.filtrarErrores(incidencias);
        if (!errores.isEmpty()) {
            throw new IncidenciasHorarioException(
                    "El movimiento se rechazó y la sesión conserva su posición anterior", errores);
        }
        try {
            sesionClaseService.guardarUbicaciones();
        } catch (DataIntegrityViolationException excepcion) {
            throw new IncidenciasHorarioException(
                    "El movimiento se rechazó porque otra sesión ocupó la misma ubicación",
                    List.of(construirChoquePorOcupacion(excepcion, sesionId)));
        }
        return new ResultadoMovimientoDTO(sesionClaseService.convertirADTO(sesion),
                validacionService.filtrarAdvertencias(incidencias));
    }

    public SesionClaseDTO devolverSesionABandeja(Long sesionId) {
        SesionClase sesion = sesionClaseService.buscar(sesionId);
        exigirBorrador(sesion.getHorario());
        sesion.desubicar();
        sesionClaseService.guardarUbicaciones();
        return sesionClaseService.convertirADTO(sesion);
    }

    public HorarioDTO aprobar(Long horarioId) {
        Horario horario = buscar(horarioId);
        if (horario.getEstado() == EstadoHorario.APROBADO) {
            throw new OperacionNoPermitidaException("El horario " + horario.getSemestre() + " ya está aprobado");
        }
        List<IncidenciaDTO> errores = validacionService.filtrarErrores(validacionService.validar(horario));
        int sinUbicar = (int) horario.getGrupos().stream()
                .flatMap(grupo -> grupo.getSesiones().stream())
                .filter(sesion -> !sesion.estaUbicada())
                .count();
        if (!errores.isEmpty() || sinUbicar > 0) {
            throw new IncidenciasHorarioException("No se puede aprobar el horario " + horario.getSemestre()
                    + ": tiene " + sinUbicar + " sesiones sin ubicar y " + errores.size()
                    + " incidencias de severidad ERROR pendientes", errores, sinUbicar);
        }
        horario.setEstado(EstadoHorario.APROBADO);
        horarioRepository.save(horario);
        return convertirADTO(horario);
    }

    public HorarioDTO devolverABorrador(Long horarioId) {
        Horario horario = buscar(horarioId);
        horario.setEstado(EstadoHorario.BORRADOR);
        horarioRepository.save(horario);
        return convertirADTO(horario);
    }

    private Horario buscar(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el horario con id " + id));
    }

    private String validarSemestre(HorarioDTO dto) {
        if (dto == null || dto.getSemestre() == null || dto.getSemestre().isBlank()) {
            throw new DatoInvalidoException("El semestre del horario es obligatorio");
        }
        String semestre = dto.getSemestre().trim();
        if (!FORMATO_SEMESTRE.matcher(semestre).matches()) {
            throw new DatoInvalidoException("El semestre debe tener el formato AAAA-1 o AAAA-2");
        }
        return semestre;
    }

    private void exigirBorrador(Horario horario) {
        if (horario.getEstado() == EstadoHorario.APROBADO) {
            throw new OperacionNoPermitidaException("El horario " + horario.getSemestre()
                    + " está aprobado y no admite cambios hasta devolverlo a borrador");
        }
    }

    private void exigirDatosMinimos(Horario horario) {
        List<String> faltantes = new ArrayList<>();
        if (asignaturaRepository.count() == 0) {
            faltantes.add("asignaturas");
        }
        if (docenteRepository.count() == 0) {
            faltantes.add("docentes");
        }
        if (aulaRepository.count() == 0) {
            faltantes.add("aulas");
        }
        if (horario.getGrupos().isEmpty()) {
            faltantes.add("grupos");
        }
        if (!faltantes.isEmpty()) {
            throw new OperacionNoPermitidaException(
                    "No se puede generar la propuesta porque faltan datos: " + String.join(", ", faltantes));
        }
    }

    private IncidenciaDTO construirChoquePorOcupacion(DataIntegrityViolationException excepcion, Long sesionId) {
        String detalle = String.valueOf(excepcion.getMostSpecificCause().getMessage()).toLowerCase(Locale.ROOT);
        boolean choqueDeDocente = detalle.contains(RESTRICCION_OCUPACION_DOCENTE);
        return new IncidenciaDTO(
                choqueDeDocente ? ValidadorChoques.CHOQUE_DOCENTE : ValidadorChoques.CHOQUE_AULA,
                SeveridadIncidencia.ERROR,
                choqueDeDocente
                        ? "El docente ya tiene otra sesión en ese día y hora"
                        : "El aula ya tiene otra sesión en ese día y hora",
                List.of(sesionId));
    }

    private List<LocalTime> convertirAHoras(List<Integer> minutos) {
        return minutos.stream().map(ReglaHorarioService::convertirAHora).toList();
    }

    private HorarioDTO convertirADTO(Horario horario) {
        return new HorarioDTO(horario.getId(), horario.getSemestre(), horario.getEstado(),
                horario.isPreseleccionAbierta());
    }
}
