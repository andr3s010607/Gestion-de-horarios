package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ValidadorAula implements Validador {

    public static final String TIPO_DE_AULA_INCORRECTO = "AUL-TIP";
    public static final String CAPACIDAD_INSUFICIENTE = "AUL-CAP";

    @Override
    public List<IncidenciaDTO> validar(Horario horario) {
        List<IncidenciaDTO> incidencias = new ArrayList<>();
        for (Grupo grupo : horario.getGrupos()) {
            for (SesionClase sesion : grupo.getSesiones()) {
                Aula aula = sesion.getAula();
                if (!sesion.estaUbicada() || aula == null) {
                    continue;
                }
                if (aula.getTipo() != grupo.getAsignatura().getTipoAulaRequerido()) {
                    incidencias.add(new IncidenciaDTO(TIPO_DE_AULA_INCORRECTO, SeveridadIncidencia.ERROR,
                            "El aula " + aula.getCodigo() + " es de tipo " + aula.getTipo() + " y la asignatura "
                                    + grupo.getAsignatura().getCodigo() + " requiere "
                                    + grupo.getAsignatura().getTipoAulaRequerido(),
                            List.of(sesion.getId())));
                }
                if (aula.getCapacidad() < grupo.getCupo()) {
                    incidencias.add(new IncidenciaDTO(CAPACIDAD_INSUFICIENTE, SeveridadIncidencia.ERROR,
                            "El aula " + aula.getCodigo() + " tiene capacidad " + aula.getCapacidad()
                                    + " y el grupo " + grupo.getAsignatura().getCodigo() + " grupo "
                                    + grupo.getNumero() + " tiene cupo " + grupo.getCupo(),
                            List.of(sesion.getId())));
                }
            }
        }
        return incidencias;
    }
}
