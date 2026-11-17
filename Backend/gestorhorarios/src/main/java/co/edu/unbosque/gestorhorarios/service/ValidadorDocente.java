package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.RestriccionHorariaDocente;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ValidadorDocente implements Validador {

    public static final String RESTRICCION_DOCENTE = "DOC-RES";
    public static final String DOCENTE_NO_HABILITADO = "DOC-HAB";

    @Override
    public List<IncidenciaDTO> validar(Horario horario) {
        List<IncidenciaDTO> incidencias = new ArrayList<>();
        for (Grupo grupo : horario.getGrupos()) {
            if (grupo.getDocente() == null) {
                registrarSesionesUbicadasSinDocente(grupo, incidencias);
                continue;
            }
            registrarFaltaDeHabilitacion(grupo, incidencias);
            registrarCrucesConRestricciones(grupo, incidencias);
        }
        return incidencias;
    }

    private void registrarSesionesUbicadasSinDocente(Grupo grupo, List<IncidenciaDTO> incidencias) {
        for (SesionClase sesion : grupo.getSesiones()) {
            if (sesion.estaUbicada()) {
                incidencias.add(new IncidenciaDTO(DOCENTE_NO_HABILITADO, SeveridadIncidencia.ERROR,
                        "El grupo " + describir(grupo)
                                + " no tiene un docente habilitado asignado y su sesión no puede ubicarse",
                        List.of(sesion.getId())));
            }
        }
    }

    private void registrarFaltaDeHabilitacion(Grupo grupo, List<IncidenciaDTO> incidencias) {
        Docente docente = grupo.getDocente();
        Long asignaturaId = grupo.getAsignatura().getId();
        boolean habilitado = docente.getHabilitaciones().stream()
                .anyMatch(habilitacion -> habilitacion.getAsignatura().getId().equals(asignaturaId));
        if (!habilitado) {
            incidencias.add(new IncidenciaDTO(DOCENTE_NO_HABILITADO, SeveridadIncidencia.ERROR,
                    "El docente " + docente.getNombre() + " no está habilitado para la asignatura "
                            + grupo.getAsignatura().getCodigo(),
                    grupo.getSesiones().stream().map(SesionClase::getId).toList()));
        }
    }

    private void registrarCrucesConRestricciones(Grupo grupo, List<IncidenciaDTO> incidencias) {
        Docente docente = grupo.getDocente();
        for (SesionClase sesion : grupo.getSesiones()) {
            if (!sesion.estaUbicada()) {
                continue;
            }
            for (RestriccionHorariaDocente restriccion : docente.getRestricciones()) {
                if (restriccion.getDia().equals(sesion.getDia())
                        && ReglaHorarioService.seSolapan(sesion.getMinutoInicio(), sesion.getMinutoFin(),
                                restriccion.getMinutoInicio(), restriccion.getMinutoFin())) {
                    incidencias.add(new IncidenciaDTO(RESTRICCION_DOCENTE, SeveridadIncidencia.ERROR,
                            "La sesión de " + describir(grupo) + " se solapa con una restricción de horario del docente "
                                    + docente.getNombre() + " el " + DiaSemana.desdeNumero(sesion.getDia()).getEtiqueta(),
                            List.of(sesion.getId())));
                    break;
                }
            }
        }
    }

    private String describir(Grupo grupo) {
        return grupo.getAsignatura().getCodigo() + " grupo " + grupo.getNumero();
    }
}
