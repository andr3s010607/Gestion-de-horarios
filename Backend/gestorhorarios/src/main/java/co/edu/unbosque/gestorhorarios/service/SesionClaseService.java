package co.edu.unbosque.gestorhorarios.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.gestorhorarios.dto.SesionClaseDTO;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.RestriccionHorariaDocente;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.TipoRestriccionHorario;
import co.edu.unbosque.gestorhorarios.repository.AsignaturaRecursoRequeridoRepository;
import co.edu.unbosque.gestorhorarios.repository.AulaRecursoRepository;
import co.edu.unbosque.gestorhorarios.repository.AulaRepository;
import co.edu.unbosque.gestorhorarios.repository.GrupoRepository;
import co.edu.unbosque.gestorhorarios.repository.HorarioRepository;
import co.edu.unbosque.gestorhorarios.repository.RestriccionHorariaDocenteRepository;
import co.edu.unbosque.gestorhorarios.repository.SesionClaseRepository;

@Service
@Transactional
public class SesionClaseService implements CRUDOperation<SesionClaseDTO> {

    private final SesionClaseRepository sesionClaseRepository;
    private final HorarioRepository horarioRepository;
    private final GrupoRepository grupoRepository;
    private final AulaRepository aulaRepository;
    private final RestriccionHorariaDocenteRepository restriccionRepository;
    private final AulaRecursoRepository aulaRecursoRepository;
    private final AsignaturaRecursoRequeridoRepository asignaturaRecursoRepository;
    private final ReglaHorarioService reglaHorarioService;

    public SesionClaseService(SesionClaseRepository sesionClaseRepository, HorarioRepository horarioRepository,
            GrupoRepository grupoRepository, AulaRepository aulaRepository,
            RestriccionHorariaDocenteRepository restriccionRepository,
            AulaRecursoRepository aulaRecursoRepository,
            AsignaturaRecursoRequeridoRepository asignaturaRecursoRepository,
            ReglaHorarioService reglaHorarioService) {
        this.sesionClaseRepository = sesionClaseRepository;
        this.horarioRepository = horarioRepository;
        this.grupoRepository = grupoRepository;
        this.aulaRepository = aulaRepository;
        this.restriccionRepository = restriccionRepository;
        this.aulaRecursoRepository = aulaRecursoRepository;
        this.asignaturaRecursoRepository = asignaturaRecursoRepository;
        this.reglaHorarioService = reglaHorarioService;
    }

    @Override
    public int create(SesionClaseDTO dto) {
        if (!validarSesion(dto, null).isEmpty()) {
            return 1;
        }
        SesionClase entity = convertirAEntidad(dto);
        if (entity == null) {
            return 1;
        }
        sesionClaseRepository.saveAndFlush(entity);
        return 0;
    }

    @Override
    public List<SesionClaseDTO> getAll() {
        return sesionClaseRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public List<SesionClaseDTO> getByHorarioId(Long horarioId) {
        return sesionClaseRepository.findByHorarioId(horarioId).stream().map(this::convertirADTO).toList();
    }

    public List<SesionClaseDTO> getByHorarioYDocente(Long horarioId, Long docenteId) {
        return sesionClaseRepository.findByHorarioId(horarioId).stream()
                .filter(sesion -> sesion.getGrupo().getDocente() != null
                        && sesion.getGrupo().getDocente().getId().equals(docenteId))
                .map(this::convertirADTO).toList();
    }

    public List<SesionClaseDTO> getByHorarioYAula(Long horarioId, Long aulaId) {
        return sesionClaseRepository.findByHorarioId(horarioId).stream()
                .filter(sesion -> sesion.getAula().getId().equals(aulaId))
                .map(this::convertirADTO).toList();
    }

    public List<SesionClaseDTO> getByHorarioYAsignatura(Long horarioId, Long asignaturaId) {
        return sesionClaseRepository.findByHorarioId(horarioId).stream()
                .filter(sesion -> sesion.getGrupo().getAsignatura().getId().equals(asignaturaId))
                .map(this::convertirADTO).toList();
    }

    public SesionClaseDTO getById(Long id) {
        return sesionClaseRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!sesionClaseRepository.existsById(id)) {
            return 1;
        }
        sesionClaseRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, SesionClaseDTO dto) {
        if (!sesionClaseRepository.existsById(id) || !validarSesion(dto, id).isEmpty()) {
            return 1;
        }
        SesionClase entity = convertirAEntidad(dto);
        if (entity == null) {
            return 1;
        }
        entity.setId(id);
        sesionClaseRepository.save(entity);
        return 0;
    }

    @Override
    public long count() {
        return sesionClaseRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return sesionClaseRepository.existsById(id);
    }

    public List<String> validarSesion(SesionClaseDTO dto, Long sesionExcluidaId) {
        List<String> errores = new ArrayList<>();
        if (dto == null) {
            errores.add("La sesión es obligatoria");
            return errores;
        }

        Horario horario = buscarHorario(dto.getHorarioId(), errores);
        Grupo grupo = buscarGrupo(dto.getGrupoId(), errores);
        Aula aula = buscarAula(dto.getAulaId(), errores);
        validarHoras(dto, errores);
        if (horario == null || grupo == null || aula == null || !errores.isEmpty()) {
            return errores;
        }

        if (!horario.getSemestre().getId().equals(grupo.getSemestre().getId())) {
            errores.add("El grupo debe pertenecer al mismo semestre del horario");
        }
        if (!grupo.isActivo() || !aula.isActiva()) {
            errores.add("El grupo y el aula deben estar activos");
        }
        if (grupo.getDocente() == null || !grupo.getDocente().isActivo()) {
            errores.add("El grupo debe tener un docente activo asignado");
        }
        validarCupo(grupo, aula, errores);
        validarRecursos(grupo, aula, errores);
        validarContraSesionesExistentes(dto, sesionExcluidaId, grupo, horario, errores);
        validarRestriccionesDocente(dto, grupo, errores);
        return errores;
    }

    private Horario buscarHorario(Long horarioId, List<String> errores) {
        Horario horario = horarioId == null ? null : horarioRepository.findById(horarioId).orElse(null);
        if (horario == null) {
            errores.add("El horario indicado no existe");
        }
        return horario;
    }

    private Grupo buscarGrupo(Long grupoId, List<String> errores) {
        Grupo grupo = grupoId == null ? null : grupoRepository.findById(grupoId).orElse(null);
        if (grupo == null) {
            errores.add("El grupo indicado no existe");
        }
        return grupo;
    }

    private Aula buscarAula(Long aulaId, List<String> errores) {
        Aula aula = aulaId == null ? null : aulaRepository.findById(aulaId).orElse(null);
        if (aula == null) {
            errores.add("El aula indicada no existe");
        }
        return aula;
    }

    private void validarHoras(SesionClaseDTO dto, List<String> errores) {
        if (dto.getDia() == null || dto.getHoraInicio() == null || dto.getHoraFin() == null) {
            errores.add("El día y las horas de inicio y fin son obligatorios");
            return;
        }
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            errores.add("La hora de fin debe ser posterior a la hora de inicio");
            return;
        }
        if (Duration.between(dto.getHoraInicio(), dto.getHoraFin()).toMinutes()
                != reglaHorarioService.duracionSesionMinutos()) {
            errores.add("Cada sesión debe durar " + reglaHorarioService.duracionSesionMinutos() + " minutos");
        }
        LocalTime inicioJornada = dto.getDia() == DiaSemana.SABADO
                ? reglaHorarioService.horaInicioSabado() : reglaHorarioService.horaInicioSemana();
        LocalTime finJornada = dto.getDia() == DiaSemana.SABADO
                ? reglaHorarioService.horaFinSabado() : reglaHorarioService.horaFinSemana();
        if (dto.getHoraInicio().isBefore(inicioJornada) || dto.getHoraFin().isAfter(finJornada)) {
            errores.add("La sesión está fuera de la franja permitida");
        }
        if (seSuperponen(dto.getHoraInicio(), dto.getHoraFin(), reglaHorarioService.horaInicioAlmuerzo(),
                reglaHorarioService.horaFinAlmuerzo())) {
            errores.add("No se permiten sesiones durante la franja de almuerzo");
        }
    }

    private void validarCupo(Grupo grupo, Aula aula, List<String> errores) {
        int cupoConTolerancia = new BigDecimal(reglaHorarioService.cupoMaximoGrupo())
                .multiply(BigDecimal.ONE.add(reglaHorarioService.toleranciaCupoPorcentaje()
                        .divide(new BigDecimal("100"))))
                .setScale(0, RoundingMode.CEILING).intValue();
        if (grupo.getCupoPlaneado() > cupoConTolerancia) {
            errores.add("El grupo supera el cupo máximo permitido con tolerancia");
        }
        if (grupo.getCupoPlaneado() > aula.getCapacidad()) {
            errores.add("El cupo planeado del grupo supera la capacidad del aula");
        }
    }

    private void validarRecursos(Grupo grupo, Aula aula, List<String> errores) {
        Set<Long> requeridos = asignaturaRecursoRepository.findAll().stream()
                .filter(relacion -> relacion.getAsignatura().getId().equals(grupo.getAsignatura().getId()))
                .map(relacion -> relacion.getRecurso().getId()).collect(java.util.stream.Collectors.toSet());
        Set<Long> disponibles = aulaRecursoRepository.findAll().stream()
                .filter(relacion -> relacion.getAula().getId().equals(aula.getId()))
                .map(relacion -> relacion.getRecurso().getId()).collect(java.util.stream.Collectors.toSet());
        if (!disponibles.containsAll(requeridos)) {
            errores.add("El aula no cuenta con todos los recursos requeridos por la asignatura");
        }
    }

    private void validarContraSesionesExistentes(SesionClaseDTO dto, Long sesionExcluidaId, Grupo grupo,
            Horario horario, List<String> errores) {
        List<SesionClase> sesiones = sesionClaseRepository.findByHorarioId(horario.getId()).stream()
                .filter(sesion -> sesionExcluidaId == null || !sesion.getId().equals(sesionExcluidaId)).toList();
        long sesionesDelGrupo = sesiones.stream()
                .filter(sesion -> sesion.getGrupo().getId().equals(grupo.getId())).count();
        if (sesionesDelGrupo + 1 > grupo.getAsignatura().getSesionesSemanales()) {
            errores.add("El grupo ya tiene todas las sesiones semanales requeridas");
        }
        for (SesionClase sesion : sesiones) {
            if (sesion.getDia() != dto.getDia()
                    || !seSuperponen(dto.getHoraInicio(), dto.getHoraFin(), sesion.getHoraInicio(), sesion.getHoraFin())) {
                continue;
            }
            if (sesion.getAula().getId().equals(dto.getAulaId())) {
                errores.add("Existe un choque con otra sesión en el aula");
            }
            if (grupo.getDocente() != null && sesion.getGrupo().getDocente() != null
                    && sesion.getGrupo().getDocente().getId().equals(grupo.getDocente().getId())) {
                errores.add("Existe un choque con otra sesión del docente");
            }
        }
        if (grupo.getAsignatura().getSesionesSemanales() == 2 || grupo.getAsignatura().getSesionesSemanales() == 3) {
            boolean mismoDia = sesiones.stream()
                    .filter(sesion -> sesion.getGrupo().getId().equals(grupo.getId()))
                    .anyMatch(sesion -> sesion.getDia() == dto.getDia());
            if (mismoDia) {
                errores.add("Las sesiones de una asignatura no pueden quedar programadas el mismo día");
            }
            boolean diaConsecutivo = sesiones.stream()
                    .filter(sesion -> sesion.getGrupo().getId().equals(grupo.getId()))
                    .anyMatch(sesion -> Math.abs(sesion.getDia().ordinal() - dto.getDia().ordinal()) == 1);
            if (diaConsecutivo) {
                errores.add("Las sesiones de una asignatura con dos o tres sesiones no pueden quedar en días consecutivos");
            }
        }
    }

    private void validarRestriccionesDocente(SesionClaseDTO dto, Grupo grupo, List<String> errores) {
        if (grupo.getDocente() == null) {
            return;
        }
        List<RestriccionHorariaDocente> restricciones = restriccionRepository.findAll().stream()
                .filter(restriccion -> restriccion.getDocente().getId().equals(grupo.getDocente().getId()))
                .filter(restriccion -> restriccion.getDia() == dto.getDia()).toList();
        boolean tieneDisponibilidades = restricciones.stream()
                .anyMatch(restriccion -> restriccion.getTipo() == TipoRestriccionHorario.DISPONIBLE);
        if (tieneDisponibilidades && restricciones.stream()
                .filter(restriccion -> restriccion.getTipo() == TipoRestriccionHorario.DISPONIBLE)
                .noneMatch(restriccion -> !dto.getHoraInicio().isBefore(restriccion.getHoraInicio())
                        && !dto.getHoraFin().isAfter(restriccion.getHoraFin()))) {
            errores.add("La sesión está fuera de la disponibilidad declarada por el docente");
        }
        if (restricciones.stream().filter(restriccion -> restriccion.getTipo() == TipoRestriccionHorario.NO_DISPONIBLE)
                .anyMatch(restriccion -> seSuperponen(dto.getHoraInicio(), dto.getHoraFin(),
                        restriccion.getHoraInicio(), restriccion.getHoraFin()))) {
            errores.add("La sesión coincide con una restricción horaria del docente");
        }
    }

    private boolean seSuperponen(LocalTime inicioUno, LocalTime finUno, LocalTime inicioDos, LocalTime finDos) {
        return inicioUno.isBefore(finDos) && finUno.isAfter(inicioDos);
    }

    private SesionClase convertirAEntidad(SesionClaseDTO dto) {
        Horario horario = dto.getHorarioId() == null ? null : horarioRepository.findById(dto.getHorarioId()).orElse(null);
        Grupo grupo = dto.getGrupoId() == null ? null : grupoRepository.findById(dto.getGrupoId()).orElse(null);
        Aula aula = dto.getAulaId() == null ? null : aulaRepository.findById(dto.getAulaId()).orElse(null);
        if (horario == null || grupo == null || aula == null) {
            return null;
        }
        return new SesionClase(horario, grupo, aula, dto.getDia(), dto.getHoraInicio(), dto.getHoraFin());
    }

    private SesionClaseDTO convertirADTO(SesionClase entity) {
        return new SesionClaseDTO(entity.getId(), entity.getHorario().getId(), entity.getGrupo().getId(),
                entity.getAula().getId(), entity.getDia(), entity.getHoraInicio(), entity.getHoraFin());
    }
}
