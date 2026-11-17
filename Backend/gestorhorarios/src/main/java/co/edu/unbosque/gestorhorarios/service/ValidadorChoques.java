package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ValidadorChoques implements Validador {

    public static final String CHOQUE_DOCENTE = "CHQ-DOC";
    public static final String CHOQUE_AULA = "CHQ-AUL";

    private static final long FACTOR_CLAVE_DIA = 10L;

    @Override
    public List<IncidenciaDTO> validar(Horario horario) {
        Map<Long, List<SesionClase>> sesionesPorDocenteYDia = new HashMap<>();
        Map<Long, List<SesionClase>> sesionesPorAulaYDia = new HashMap<>();
        for (Grupo grupo : horario.getGrupos()) {
            for (SesionClase sesion : grupo.getSesiones()) {
                if (!sesion.estaUbicada()) {
                    continue;
                }
                if (grupo.getDocente() != null) {
                    agrupar(sesionesPorDocenteYDia, grupo.getDocente().getId(), sesion);
                }
                if (sesion.getAula() != null) {
                    agrupar(sesionesPorAulaYDia, sesion.getAula().getId(), sesion);
                }
            }
        }
        List<IncidenciaDTO> incidencias = new ArrayList<>();
        for (List<SesionClase> sesiones : sesionesPorDocenteYDia.values()) {
            registrarSolapes(sesiones, CHOQUE_DOCENTE, incidencias);
        }
        for (List<SesionClase> sesiones : sesionesPorAulaYDia.values()) {
            registrarSolapes(sesiones, CHOQUE_AULA, incidencias);
        }
        return incidencias;
    }

    private void agrupar(Map<Long, List<SesionClase>> sesionesPorRecursoYDia, Long recursoId, SesionClase sesion) {
        long clave = recursoId * FACTOR_CLAVE_DIA + sesion.getDia();
        sesionesPorRecursoYDia.computeIfAbsent(clave, claveNueva -> new ArrayList<>()).add(sesion);
    }

    private void registrarSolapes(List<SesionClase> sesiones, String codigo, List<IncidenciaDTO> incidencias) {
        for (int primera = 0; primera < sesiones.size(); primera++) {
            for (int segunda = primera + 1; segunda < sesiones.size(); segunda++) {
                SesionClase una = sesiones.get(primera);
                SesionClase otra = sesiones.get(segunda);
                if (una.getMinutoInicio() < otra.getMinutoFin() && otra.getMinutoInicio() < una.getMinutoFin()) {
                    incidencias.add(new IncidenciaDTO(codigo, SeveridadIncidencia.ERROR,
                            construirMensaje(codigo, una, otra), List.of(una.getId(), otra.getId())));
                }
            }
        }
    }

    private String construirMensaje(String codigo, SesionClase una, SesionClase otra) {
        String recurso = CHOQUE_DOCENTE.equals(codigo)
                ? "El docente " + una.getGrupo().getDocente().getNombre()
                : "El aula " + una.getAula().getCodigo();
        return recurso + " tiene dos sesiones que se solapan el " + DiaSemana.desdeNumero(una.getDia()).getEtiqueta()
                + ": " + describir(una) + " y " + describir(otra);
    }

    private String describir(SesionClase sesion) {
        return sesion.getGrupo().getAsignatura().getCodigo() + " grupo " + sesion.getGrupo().getNumero();
    }
}
