package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ValidacionService {

    private final List<Validador> validadores;

    public ValidacionService(List<Validador> validadores) {
        this.validadores = validadores;
    }

    public List<IncidenciaDTO> validar(Horario horario) {
        List<IncidenciaDTO> incidencias = new ArrayList<>();
        for (Validador validador : validadores) {
            incidencias.addAll(validador.validar(horario));
        }
        return incidencias;
    }

    public List<IncidenciaDTO> validarSesiones(Horario horario, Collection<Long> sesionIds) {
        return validar(horario).stream()
                .filter(incidencia -> incidencia.getSesionIds().stream().anyMatch(sesionIds::contains))
                .toList();
    }

    public List<IncidenciaDTO> filtrarErrores(List<IncidenciaDTO> incidencias) {
        return filtrarPorSeveridad(incidencias, SeveridadIncidencia.ERROR);
    }

    public List<IncidenciaDTO> filtrarAdvertencias(List<IncidenciaDTO> incidencias) {
        return filtrarPorSeveridad(incidencias, SeveridadIncidencia.ADVERTENCIA);
    }

    private List<IncidenciaDTO> filtrarPorSeveridad(List<IncidenciaDTO> incidencias, SeveridadIncidencia severidad) {
        return incidencias.stream().filter(incidencia -> incidencia.getSeveridad() == severidad).toList();
    }
}
