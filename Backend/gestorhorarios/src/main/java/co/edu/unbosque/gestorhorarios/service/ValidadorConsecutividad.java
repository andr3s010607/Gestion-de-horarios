package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ValidadorConsecutividad implements Validador {

    public static final String DIAS_CONSECUTIVOS = "CON-DIA";

    private static final int MINIMO_SESIONES_EVALUADAS = 2;
    private static final int MAXIMO_SESIONES_EVALUADAS = 3;

    @Override
    public List<IncidenciaDTO> validar(Horario horario) {
        List<IncidenciaDTO> incidencias = new ArrayList<>();
        for (Grupo grupo : horario.getGrupos()) {
            int sesionesSemana = grupo.getAsignatura().getSesionesSemana();
            if (sesionesSemana < MINIMO_SESIONES_EVALUADAS || sesionesSemana > MAXIMO_SESIONES_EVALUADAS) {
                continue;
            }
            List<SesionClase> ubicadas = grupo.getSesiones().stream().filter(SesionClase::estaUbicada).toList();
            for (int primera = 0; primera < ubicadas.size(); primera++) {
                for (int segunda = primera + 1; segunda < ubicadas.size(); segunda++) {
                    SesionClase una = ubicadas.get(primera);
                    SesionClase otra = ubicadas.get(segunda);
                    if (Math.abs(una.getDia() - otra.getDia()) == 1) {
                        incidencias.add(new IncidenciaDTO(DIAS_CONSECUTIVOS, SeveridadIncidencia.ADVERTENCIA,
                                "La asignatura " + grupo.getAsignatura().getCodigo() + " grupo " + grupo.getNumero()
                                        + " tiene sesiones en días consecutivos: "
                                        + DiaSemana.desdeNumero(Math.min(una.getDia(), otra.getDia())).getEtiqueta()
                                        + " y "
                                        + DiaSemana.desdeNumero(Math.max(una.getDia(), otra.getDia())).getEtiqueta(),
                                List.of(una.getId(), otra.getId())));
                    }
                }
            }
        }
        return incidencias;
    }
}
