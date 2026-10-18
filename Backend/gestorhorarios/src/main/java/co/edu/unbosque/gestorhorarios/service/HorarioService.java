package co.edu.unbosque.gestorhorarios.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.gestorhorarios.dto.HorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.ResultadoGeneracionHorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.SesionClaseDTO;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.Semestre;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.EstadoHorario;
import co.edu.unbosque.gestorhorarios.repository.AulaRepository;
import co.edu.unbosque.gestorhorarios.repository.GrupoRepository;
import co.edu.unbosque.gestorhorarios.repository.HorarioRepository;
import co.edu.unbosque.gestorhorarios.repository.SemestreRepository;
import co.edu.unbosque.gestorhorarios.repository.SesionClaseRepository;

@Service
@Transactional
public class HorarioService implements CRUDOperation<HorarioDTO> {

    private final HorarioRepository horarioRepository;
    private final SemestreRepository semestreRepository;
    private final GrupoRepository grupoRepository;
    private final AulaRepository aulaRepository;
    private final SesionClaseRepository sesionClaseRepository;
    private final SesionClaseService sesionClaseService;
    private final ReglaHorarioService reglaHorarioService;

    public HorarioService(HorarioRepository horarioRepository, SemestreRepository semestreRepository,
            GrupoRepository grupoRepository, AulaRepository aulaRepository,
            SesionClaseRepository sesionClaseRepository, SesionClaseService sesionClaseService,
            ReglaHorarioService reglaHorarioService) {
        this.horarioRepository = horarioRepository;
        this.semestreRepository = semestreRepository;
        this.grupoRepository = grupoRepository;
        this.aulaRepository = aulaRepository;
        this.sesionClaseRepository = sesionClaseRepository;
        this.sesionClaseService = sesionClaseService;
        this.reglaHorarioService = reglaHorarioService;
    }

    @Override
    public int create(HorarioDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            Horario entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            horarioRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<HorarioDTO> getAll() {
        return horarioRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public HorarioDTO getById(Long id) {
        return horarioRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!horarioRepository.existsById(id)) {
            return 1;
        }
        horarioRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, HorarioDTO dto) {
        try {
            if (!horarioRepository.existsById(id) || dto == null) {
                return 1;
            }
            Horario entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            horarioRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return horarioRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return horarioRepository.existsById(id);
    }

    public ResultadoGeneracionHorarioDTO generarPropuesta(Long horarioId) {
        Horario horario = horarioRepository.findById(horarioId).orElse(null);
        if (horario == null) {
            return new ResultadoGeneracionHorarioDTO(0, List.of("El horario indicado no existe"));
        }

        int creadas = 0;
        List<String> mensajes = new ArrayList<>();
        List<Grupo> grupos = gruposDelSemestre(horario.getSemestre().getId());
        List<Aula> aulas = aulaRepository.findAll().stream().filter(Aula::isActiva)
                .sorted(Comparator.comparing(Aula::getId)).toList();

        for (Grupo grupo : grupos) {
            int existentes = (int) sesionClaseRepository.findByHorarioId(horarioId).stream()
                    .filter(sesion -> sesion.getGrupo().getId().equals(grupo.getId())).count();
            int pendientes = grupo.getAsignatura().getSesionesSemanales() - existentes;
            while (pendientes > 0) {
                SesionClaseDTO propuesta = buscarEspacioDisponible(horario, grupo, aulas);
                if (propuesta == null) {
                    mensajes.add("No fue posible asignar todas las sesiones para el grupo " + grupo.getCodigo());
                    break;
                }
                if (sesionClaseService.create(propuesta) == 0) {
                    creadas++;
                    pendientes--;
                } else {
                    mensajes.add("No fue posible guardar una sesión para el grupo " + grupo.getCodigo());
                    break;
                }
            }
        }
        if (creadas > 0) {
            horario.setEstado(EstadoHorario.GENERADO);
            horarioRepository.save(horario);
        }
        mensajes.addAll(validarHorario(horarioId));
        return new ResultadoGeneracionHorarioDTO(creadas, mensajes.stream().distinct().toList());
    }

    public List<String> validarHorario(Long horarioId) {
        Horario horario = horarioRepository.findById(horarioId).orElse(null);
        if (horario == null) {
            return List.of("El horario indicado no existe");
        }
        List<String> errores = new ArrayList<>();
        List<SesionClase> sesiones = sesionClaseRepository.findByHorarioId(horarioId);
        for (Grupo grupo : gruposDelSemestre(horario.getSemestre().getId())) {
            errores.addAll(sesionClaseService.validarGrupoParaProgramacion(grupo));
            long cantidad = sesiones.stream().filter(sesion -> sesion.getGrupo().getId().equals(grupo.getId())).count();
            if (cantidad != grupo.getAsignatura().getSesionesSemanales()) {
                errores.add("El grupo " + grupo.getCodigo() + " requiere "
                        + grupo.getAsignatura().getSesionesSemanales() + " sesiones y tiene " + cantidad);
            }
        }
        for (SesionClase sesion : sesiones) {
            SesionClaseDTO dto = new SesionClaseDTO(sesion.getId(), horarioId, sesion.getGrupo().getId(),
                    sesion.getAula().getId(), sesion.getDia(), sesion.getHoraInicio(), sesion.getHoraFin());
            errores.addAll(sesionClaseService.validarSesion(dto, sesion.getId()));
        }
        return errores.stream().distinct().toList();
    }

    public List<SesionClaseDTO> getSesiones(Long horarioId) {
        return sesionClaseService.getByHorarioId(horarioId);
    }

    public List<SesionClaseDTO> getSesionesPorDocente(Long horarioId, Long docenteId) {
        return sesionClaseService.getByHorarioYDocente(horarioId, docenteId);
    }

    public List<SesionClaseDTO> getSesionesPorAula(Long horarioId, Long aulaId) {
        return sesionClaseService.getByHorarioYAula(horarioId, aulaId);
    }

    public List<SesionClaseDTO> getSesionesPorAsignatura(Long horarioId, Long asignaturaId) {
        return sesionClaseService.getByHorarioYAsignatura(horarioId, asignaturaId);
    }

    public byte[] exportarCsv(Long horarioId) {
        Horario horario = horarioRepository.findById(horarioId).orElse(null);
        if (horario == null) {
            return null;
        }
        StringBuilder csv = new StringBuilder("Horario,Día,Hora inicio,Hora fin,Grupo,Asignatura,Docente,Aula\n");
        sesionClaseRepository.findByHorarioId(horarioId).stream()
                .sorted(Comparator.comparing(SesionClase::getDia).thenComparing(SesionClase::getHoraInicio))
                .forEach(sesion -> csv.append(escapar(horario.getNombre())).append(',')
                        .append(sesion.getDia()).append(',').append(sesion.getHoraInicio()).append(',')
                        .append(sesion.getHoraFin()).append(',').append(escapar(sesion.getGrupo().getCodigo())).append(',')
                        .append(escapar(sesion.getGrupo().getAsignatura().getNombre())).append(',')
                        .append(escapar(nombreDocente(sesion))).append(',')
                        .append(escapar(sesion.getAula().getCodigo())).append('\n'));
        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private SesionClaseDTO buscarEspacioDisponible(Horario horario, Grupo grupo, List<Aula> aulas) {
        for (DiaSemana dia : diasPreferidos(grupo.getAsignatura().getSesionesSemanales())) {
            LocalTime inicio = dia == DiaSemana.SABADO ? reglaHorarioService.horaInicioSabado()
                    : reglaHorarioService.horaInicioSemana();
            LocalTime finJornada = dia == DiaSemana.SABADO ? reglaHorarioService.horaFinSabado()
                    : reglaHorarioService.horaFinSemana();
            while (!inicio.plusMinutes(reglaHorarioService.duracionSesionMinutos()).isAfter(finJornada)) {
                LocalTime fin = inicio.plusMinutes(reglaHorarioService.duracionSesionMinutos());
                for (Aula aula : aulas) {
                    SesionClaseDTO candidata = new SesionClaseDTO(null, horario.getId(), grupo.getId(), aula.getId(), dia,
                            inicio, fin);
                    if (sesionClaseService.validarSesion(candidata, null).isEmpty()) {
                        return candidata;
                    }
                }
                inicio = fin;
            }
        }
        return null;
    }

    private List<DiaSemana> diasPreferidos(int sesionesSemanales) {
        if (sesionesSemanales == 2) {
            return List.of(DiaSemana.LUNES, DiaSemana.MIERCOLES, DiaSemana.VIERNES, DiaSemana.SABADO,
                    DiaSemana.MARTES, DiaSemana.JUEVES);
        }
        if (sesionesSemanales == 3) {
            return List.of(DiaSemana.LUNES, DiaSemana.MIERCOLES, DiaSemana.VIERNES, DiaSemana.SABADO,
                    DiaSemana.MARTES, DiaSemana.JUEVES);
        }
        return List.of(DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES, DiaSemana.JUEVES,
                DiaSemana.VIERNES, DiaSemana.SABADO);
    }

    private List<Grupo> gruposDelSemestre(Long semestreId) {
        return grupoRepository.findAll().stream().filter(Grupo::isActivo)
                .filter(grupo -> grupo.getSemestre().getId().equals(semestreId)).toList();
    }

    private String nombreDocente(SesionClase sesion) {
        if (sesion.getGrupo().getDocente() == null) {
            return "";
        }
        return sesion.getGrupo().getDocente().getNombres() + " " + sesion.getGrupo().getDocente().getApellidos();
    }

    private String escapar(String valor) {
        return '"' + (valor == null ? "" : valor.replace("\"", "\"\"")) + '"';
    }

    private Horario convertirAEntidad(HorarioDTO dto) {
        Horario entity = new Horario();
        entity.setNombre(dto.getNombre());
        entity.setEstado(dto.getEstado() == null ? EstadoHorario.BORRADOR : dto.getEstado());
        if (dto.getFechaCreacion() != null) {
            entity.setFechaCreacion(dto.getFechaCreacion());
        }
        Semestre semestre = dto.getSemestreId() == null ? null : semestreRepository.findById(dto.getSemestreId()).orElse(null);
        if (semestre == null) {
            return null;
        }
        entity.setSemestre(semestre);
        return entity;
    }

    private HorarioDTO convertirADTO(Horario entity) {
        return new HorarioDTO(entity.getId(), entity.getNombre(), entity.getSemestre().getId(), entity.getEstado(),
                entity.getFechaCreacion());
    }
}
