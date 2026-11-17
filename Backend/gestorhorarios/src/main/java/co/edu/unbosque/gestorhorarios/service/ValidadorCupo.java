package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ValidadorCupo implements Validador {

    public static final String CUPO_EXCEDIDO = "CUP-EXC";

    private final ReglaHorarioService reglaHorarioService;

    public ValidadorCupo(ReglaHorarioService reglaHorarioService) {
        this.reglaHorarioService = reglaHorarioService;
    }

    @Override
    public List<IncidenciaDTO> validar(Horario horario) {
        int cupoConTolerancia = reglaHorarioService.cupoMaximoConTolerancia();
        List<IncidenciaDTO> incidencias = new ArrayList<>();
        for (Grupo grupo : horario.getGrupos()) {
            if (grupo.getCupo() > cupoConTolerancia) {
                incidencias.add(new IncidenciaDTO(CUPO_EXCEDIDO, SeveridadIncidencia.ERROR,
                        "El cupo " + grupo.getCupo() + " del grupo " + grupo.getAsignatura().getCodigo() + " grupo "
                                + grupo.getNumero() + " supera el cupo máximo con tolerancia de " + cupoConTolerancia,
                        grupo.getSesiones().stream().map(SesionClase::getId).toList()));
            }
        }
        return incidencias;
    }
}
