package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.enums.EstadoHorario;
import co.edu.unbosque.gestorhorarios.exception.IncidenciasHorarioException;
import co.edu.unbosque.gestorhorarios.repository.HorarioRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hace cumplir el RF-28 para los datos de los que depende un horario aprobado
 * (aulas, asignaturas, restricciones, habilitaciones y parámetros): el cambio
 * se aplica, se vuelven a validar los horarios aprobados y, si aparece alguna
 * incidencia de severidad ERROR que antes no existía, el cambio se deshace.
 */
@Service
@Transactional
public class ProteccionHorarioAprobadoService {

    private final HorarioRepository horarioRepository;
    private final ValidacionService validacionService;
    private final EntityManager entityManager;

    public ProteccionHorarioAprobadoService(HorarioRepository horarioRepository,
            ValidacionService validacionService, EntityManager entityManager) {
        this.horarioRepository = horarioRepository;
        this.validacionService = validacionService;
        this.entityManager = entityManager;
    }

    public <T> T aplicarSinDanarHorariosAprobados(Supplier<T> cambio) {
        Map<Long, Set<String>> erroresPrevios = erroresDeHorariosAprobados();
        T resultado = cambio.get();
        // Se envía el cambio a la base y se descartan las entidades en memoria, para que
        // la validación lea de nuevo las colecciones (restricciones, habilitaciones) ya modificadas.
        entityManager.flush();
        entityManager.clear();
        for (Horario horario : horarioRepository.findByEstado(EstadoHorario.APROBADO)) {
            Set<String> previos = erroresPrevios.getOrDefault(horario.getId(), Set.of());
            List<IncidenciaDTO> nuevos = new ArrayList<>();
            for (IncidenciaDTO error : validacionService.filtrarErrores(validacionService.validar(horario))) {
                if (!previos.contains(clave(error))) {
                    nuevos.add(error);
                }
            }
            if (!nuevos.isEmpty()) {
                throw new IncidenciasHorarioException("El cambio se rechazó porque genera incidencias en el horario "
                        + "aprobado " + horario.getSemestre() + ". Devuélvelo a borrador para hacer este cambio",
                        nuevos);
            }
        }
        return resultado;
    }

    public void aplicarSinDanarHorariosAprobados(Runnable cambio) {
        aplicarSinDanarHorariosAprobados(() -> {
            cambio.run();
            return null;
        });
    }

    private Map<Long, Set<String>> erroresDeHorariosAprobados() {
        Map<Long, Set<String>> errores = new HashMap<>();
        for (Horario horario : horarioRepository.findByEstado(EstadoHorario.APROBADO)) {
            errores.put(horario.getId(), validacionService.filtrarErrores(validacionService.validar(horario))
                    .stream().map(ProteccionHorarioAprobadoService::clave).collect(Collectors.toSet()));
        }
        return errores;
    }

    private static String clave(IncidenciaDTO incidencia) {
        return incidencia.getCodigo() + incidencia.getSesionIds().stream().sorted().toList();
    }
}
