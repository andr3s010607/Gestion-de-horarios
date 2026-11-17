package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.AsignaturaPreseleccionDTO;
import co.edu.unbosque.gestorhorarios.dto.DemandaGrupoDTO;
import co.edu.unbosque.gestorhorarios.dto.GrupoPreseleccionDTO;
import co.edu.unbosque.gestorhorarios.dto.HorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.dto.PreseleccionDTO;
import co.edu.unbosque.gestorhorarios.dto.SesionClaseDTO;
import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.Preseleccion;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.entity.Usuario;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.RolUsuario;
import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import co.edu.unbosque.gestorhorarios.exception.AccesoDenegadoException;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.exception.IncidenciasHorarioException;
import co.edu.unbosque.gestorhorarios.exception.OperacionNoPermitidaException;
import co.edu.unbosque.gestorhorarios.exception.RecursoNoEncontradoException;
import co.edu.unbosque.gestorhorarios.repository.GrupoRepository;
import co.edu.unbosque.gestorhorarios.repository.HorarioRepository;
import co.edu.unbosque.gestorhorarios.repository.PreseleccionRepository;
import co.edu.unbosque.gestorhorarios.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Preselección de grupos (RF-31 a RF-35). El estudiante elige como máximo un
 * grupo por asignatura en un horario con la preselección abierta y confirma
 * solo una combinación sin choques. La preselección informa la demanda de cada
 * grupo: no descuenta cupo, no es una inscripción y no bloquea el ajuste ni la
 * aprobación del horario.
 */
@Service
@Transactional
public class PreseleccionService {

    public static final String CHOQUE_PRESELECCION = "PRE-CHQ";

    private final HorarioRepository horarioRepository;
    private final GrupoRepository grupoRepository;
    private final PreseleccionRepository preseleccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final SesionClaseService sesionClaseService;

    public PreseleccionService(HorarioRepository horarioRepository, GrupoRepository grupoRepository,
            PreseleccionRepository preseleccionRepository, UsuarioRepository usuarioRepository,
            SesionClaseService sesionClaseService) {
        this.horarioRepository = horarioRepository;
        this.grupoRepository = grupoRepository;
        this.preseleccionRepository = preseleccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.sesionClaseService = sesionClaseService;
    }

    // ----- Administrador (RF-31, RF-35) -----

    public HorarioDTO abrir(Long horarioId) {
        Horario horario = buscarHorario(horarioId);
        if (horario.isPreseleccionAbierta()) {
            throw new OperacionNoPermitidaException(
                    "La preselección del horario " + horario.getSemestre() + " ya está abierta");
        }
        long sinUbicar = sesiones(horario).stream().filter(sesion -> !sesion.estaUbicada()).count();
        if (sinUbicar > 0) {
            throw new OperacionNoPermitidaException("No se puede abrir la preselección del horario "
                    + horario.getSemestre() + ": tiene " + sinUbicar
                    + (sinUbicar == 1 ? " sesión sin ubicar" : " sesiones sin ubicar"));
        }
        horario.setPreseleccionAbierta(true);
        horarioRepository.save(horario);
        return convertirADTO(horario);
    }

    public HorarioDTO cerrar(Long horarioId) {
        Horario horario = buscarHorario(horarioId);
        if (!horario.isPreseleccionAbierta()) {
            throw new OperacionNoPermitidaException(
                    "La preselección del horario " + horario.getSemestre() + " ya está cerrada");
        }
        horario.setPreseleccionAbierta(false);
        horarioRepository.save(horario);
        return convertirADTO(horario);
    }

    @Transactional(readOnly = true)
    public List<DemandaGrupoDTO> demanda(Long horarioId) {
        buscarHorario(horarioId);
        Map<Long, Long> conteo = new HashMap<>();
        for (Object[] fila : preseleccionRepository.contarPorGrupoEnHorario(horarioId)) {
            conteo.put((Long) fila[0], ((Number) fila[1]).longValue());
        }
        return grupoRepository.findByHorarioId(horarioId).stream()
                .sorted(ORDEN_GRUPOS)
                .map(grupo -> new DemandaGrupoDTO(grupo.getId(), grupo.getAsignatura().getId(),
                        grupo.getAsignatura().getCodigo(), grupo.getAsignatura().getNombre(), grupo.getNumero(),
                        grupo.getCupo(), conteo.getOrDefault(grupo.getId(), 0L)))
                .toList();
    }

    // ----- Estudiante (RF-32 a RF-34) -----

    /**
     * Horarios que el estudiante puede consultar: los que tienen la preselección
     * abierta y, en solo lectura, los ya cerrados donde dejó una preselección confirmada.
     */
    @Transactional(readOnly = true)
    public List<HorarioDTO> horariosVisibles(Authentication autenticacion) {
        Usuario estudiante = estudianteActual(autenticacion);
        Set<Long> conPreseleccion = new LinkedHashSet<>(
                preseleccionRepository.findHorarioIdsConPreseleccionDeUsuario(estudiante.getId()));
        return horarioRepository.findAll().stream()
                .filter(horario -> horario.isPreseleccionAbierta() || conPreseleccion.contains(horario.getId()))
                .sorted(Comparator.comparing(Horario::isPreseleccionAbierta).reversed()
                        .thenComparing(Comparator.comparing(Horario::getSemestre).reversed()))
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Con la preselección abierta devuelve todos los grupos del horario. Cerrada,
     * devuelve solo los grupos que el estudiante confirmó, para consultarlos en
     * solo lectura; si no confirmó ninguno, el horario no está disponible.
     */
    @Transactional(readOnly = true)
    public List<AsignaturaPreseleccionDTO> gruposDisponibles(Authentication autenticacion, Long horarioId) {
        Horario horario = buscarHorario(horarioId);
        Set<Long> visibles;
        if (horario.isPreseleccionAbierta()) {
            visibles = null;
        } else {
            visibles = new LinkedHashSet<>(preseleccionRepository.findGrupoIdsDeUsuarioEnHorario(
                    estudianteActual(autenticacion).getId(), horarioId));
            if (visibles.isEmpty()) {
                exigirAbierta(horario);
            }
        }
        Map<Asignatura, List<GrupoPreseleccionDTO>> porAsignatura = new LinkedHashMap<>();
        grupoRepository.findByHorarioId(horario.getId()).stream()
                .filter(grupo -> visibles == null || visibles.contains(grupo.getId()))
                .sorted(ORDEN_GRUPOS)
                .forEach(grupo -> porAsignatura.computeIfAbsent(grupo.getAsignatura(), clave -> new ArrayList<>())
                        .add(new GrupoPreseleccionDTO(grupo.getId(), grupo.getNumero(), grupo.getCupo(),
                                grupo.getDocente() == null ? null : grupo.getDocente().getNombre(),
                                sesionClaseService.ordenarYConvertir(grupo.getSesiones().stream()
                                        .filter(SesionClase::estaUbicada).toList()))));
        return porAsignatura.entrySet().stream()
                .map(entrada -> new AsignaturaPreseleccionDTO(entrada.getKey().getId(), entrada.getKey().getCodigo(),
                        entrada.getKey().getNombre(), entrada.getValue()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IncidenciaDTO> revisarBorrador(Long horarioId, PreseleccionDTO borrador) {
        Horario horario = exigirAbierta(buscarHorario(horarioId));
        return choques(gruposDelBorrador(horario, borrador));
    }

    @Transactional(readOnly = true)
    public PreseleccionDTO miPreseleccion(Authentication autenticacion, Long horarioId) {
        Usuario estudiante = estudianteActual(autenticacion);
        buscarHorario(horarioId);
        return new PreseleccionDTO(horarioId,
                preseleccionRepository.findGrupoIdsDeUsuarioEnHorario(estudiante.getId(), horarioId));
    }

    /**
     * Sesiones de los grupos que el estudiante confirmó en el horario, con su
     * ubicación vigente, para exportarlas. Funciona con la preselección abierta o cerrada.
     */
    @Transactional(readOnly = true)
    public List<SesionClaseDTO> sesionesDeMiPreseleccion(Authentication autenticacion, Long horarioId) {
        Usuario estudiante = estudianteActual(autenticacion);
        Horario horario = buscarHorario(horarioId);
        List<Long> grupoIds = preseleccionRepository.findGrupoIdsDeUsuarioEnHorario(estudiante.getId(), horarioId);
        if (grupoIds.isEmpty()) {
            throw new OperacionNoPermitidaException(
                    "No tienes una preselección confirmada en el horario " + horario.getSemestre());
        }
        return sesionClaseService.ordenarYConvertir(grupoRepository.findAllById(grupoIds).stream()
                .flatMap(grupo -> grupo.getSesiones().stream())
                .filter(SesionClase::estaUbicada)
                .toList());
    }

    public PreseleccionDTO confirmar(Authentication autenticacion, Long horarioId, PreseleccionDTO borrador) {
        Usuario estudiante = estudianteActual(autenticacion);
        Horario horario = buscarHorario(horarioId);
        if (!horario.isPreseleccionAbierta()) {
            throw new OperacionNoPermitidaException("La preselección del horario " + horario.getSemestre()
                    + " está cerrada; no se guardó ningún cambio");
        }
        List<Grupo> grupos = gruposDelBorrador(horario, borrador);
        List<IncidenciaDTO> choques = choques(grupos);
        if (!choques.isEmpty()) {
            throw new IncidenciasHorarioException(
                    "No se puede confirmar la preselección porque los grupos elegidos se cruzan", choques);
        }
        preseleccionRepository.deleteDeUsuarioEnHorario(estudiante.getId(), horario.getId());
        for (Grupo grupo : grupos) {
            preseleccionRepository.save(new Preseleccion(estudiante, grupo));
        }
        return new PreseleccionDTO(horario.getId(), grupos.stream().map(Grupo::getId).toList());
    }

    // ----- Reglas -----

    /** Resuelve los grupos del borrador y exige como máximo uno por asignatura (RF-33). */
    private List<Grupo> gruposDelBorrador(Horario horario, PreseleccionDTO borrador) {
        List<Long> ids = borrador == null || borrador.getGrupoIds() == null ? List.of() : borrador.getGrupoIds();
        Set<Long> sinRepetir = new LinkedHashSet<>(ids);
        List<Grupo> grupos = new ArrayList<>();
        Map<Long, Grupo> porAsignatura = new HashMap<>();
        for (Long grupoId : sinRepetir) {
            if (grupoId == null) {
                throw new DatoInvalidoException("La preselección contiene un grupo sin identificador");
            }
            Grupo grupo = grupoRepository.findById(grupoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe el grupo con id " + grupoId));
            if (!grupo.getHorario().getId().equals(horario.getId())) {
                throw new DatoInvalidoException("El grupo " + etiqueta(grupo) + " no pertenece al horario "
                        + horario.getSemestre());
            }
            Grupo previo = porAsignatura.putIfAbsent(grupo.getAsignatura().getId(), grupo);
            if (previo != null) {
                throw new DatoInvalidoException("Solo puedes elegir un grupo por asignatura: elegiste "
                        + etiqueta(previo) + " y " + etiqueta(grupo));
            }
            grupos.add(grupo);
        }
        return grupos;
    }

    /** Señala como choque cada par de grupos con sesiones que se solapan el mismo día (RF-33). */
    private List<IncidenciaDTO> choques(List<Grupo> grupos) {
        List<IncidenciaDTO> choques = new ArrayList<>();
        for (int i = 0; i < grupos.size(); i++) {
            for (int j = i + 1; j < grupos.size(); j++) {
                for (SesionClase una : grupos.get(i).getSesiones()) {
                    for (SesionClase otra : grupos.get(j).getSesiones()) {
                        if (seCruzan(una, otra)) {
                            int inicio = Math.max(una.getMinutoInicio(), otra.getMinutoInicio());
                            int fin = Math.min(una.getMinutoFin(), otra.getMinutoFin());
                            choques.add(new IncidenciaDTO(CHOQUE_PRESELECCION, SeveridadIncidencia.ERROR,
                                    etiqueta(grupos.get(i)) + " y " + etiqueta(grupos.get(j)) + " se cruzan el "
                                            + DiaSemana.desdeNumero(una.getDia()).getEtiqueta().toLowerCase()
                                            + " de " + ReglaHorarioService.convertirAHora(inicio) + " a "
                                            + ReglaHorarioService.convertirAHora(fin),
                                    List.of(una.getId(), otra.getId())));
                        }
                    }
                }
            }
        }
        return choques;
    }

    private static boolean seCruzan(SesionClase una, SesionClase otra) {
        return una.estaUbicada() && otra.estaUbicada() && una.getDia().equals(otra.getDia())
                && ReglaHorarioService.seSolapan(una.getMinutoInicio(), una.getMinutoFin(),
                        otra.getMinutoInicio(), otra.getMinutoFin());
    }

    private static final Comparator<Grupo> ORDEN_GRUPOS = Comparator
            .comparing((Grupo grupo) -> grupo.getAsignatura().getCodigo())
            .thenComparing(Grupo::getNumero);

    private static String etiqueta(Grupo grupo) {
        return grupo.getAsignatura().getCodigo() + " G" + grupo.getNumero();
    }

    private Horario exigirAbierta(Horario horario) {
        if (!horario.isPreseleccionAbierta()) {
            throw new OperacionNoPermitidaException(
                    "La preselección del horario " + horario.getSemestre() + " no está abierta");
        }
        return horario;
    }

    private Usuario estudianteActual(Authentication autenticacion) {
        Usuario usuario = autenticacion == null ? null
                : usuarioRepository.findByNombreUsuario(autenticacion.getName()).orElse(null);
        if (usuario == null || usuario.getRol() != RolUsuario.ESTUDIANTE) {
            throw new AccesoDenegadoException("Solo un estudiante puede preseleccionar grupos");
        }
        return usuario;
    }

    private Horario buscarHorario(Long horarioId) {
        return horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el horario con id " + horarioId));
    }

    private List<SesionClase> sesiones(Horario horario) {
        return horario.getGrupos().stream().flatMap(grupo -> grupo.getSesiones().stream()).toList();
    }

    private HorarioDTO convertirADTO(Horario horario) {
        return new HorarioDTO(horario.getId(), horario.getSemestre(), horario.getEstado(),
                horario.isPreseleccionAbierta());
    }
}
