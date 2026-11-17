package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import co.edu.unbosque.gestorhorarios.enums.TipoAula;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeneradorHorario {

    private final ValidacionService validacionService;
    private final ReglaHorarioService reglaHorarioService;
    private final long limiteSegundos;

    public GeneradorHorario(ValidacionService validacionService, ReglaHorarioService reglaHorarioService,
            @Value("${app.generacion.limite-segundos}") long limiteSegundos) {
        this.validacionService = validacionService;
        this.reglaHorarioService = reglaHorarioService;
        this.limiteSegundos = limiteSegundos;
    }

    public Map<SesionClase, IncidenciaDTO> ubicarSesionesPendientes(Horario horario, List<Aula> aulas) {
        long instanteLimite = System.nanoTime() + TimeUnit.SECONDS.toNanos(limiteSegundos);
        int duracion = reglaHorarioService.duracionSesionMinutos();
        Map<DiaSemana, List<Integer>> iniciosPorDia = new EnumMap<>(DiaSemana.class);
        for (DiaSemana dia : DiaSemana.values()) {
            iniciosPorDia.put(dia, reglaHorarioService.minutosInicioValidos(dia.getNumero()));
        }
        List<SesionClase> pendientes = horario.getGrupos().stream()
                .flatMap(grupo -> grupo.getSesiones().stream())
                .filter(sesion -> !sesion.estaUbicada())
                .sorted(ordenDeMasRestringidaAMenosRestringida())
                .toList();
        Map<SesionClase, IncidenciaDTO> causasSinUbicar = new LinkedHashMap<>();
        for (SesionClase sesion : pendientes) {
            IncidenciaDTO causa = ubicar(sesion, horario, aulas, iniciosPorDia, duracion, instanteLimite);
            if (causa != null) {
                causasSinUbicar.put(sesion, causa);
            }
        }
        return causasSinUbicar;
    }

    private Comparator<SesionClase> ordenDeMasRestringidaAMenosRestringida() {
        return Comparator
                .comparing((SesionClase sesion) -> !exigeAulaEspecial(sesion.getGrupo()))
                .thenComparing(sesion -> !tieneDocenteConRestricciones(sesion.getGrupo()))
                .thenComparing(sesion -> sesion.getGrupo().getCupo(), Comparator.reverseOrder())
                .thenComparing(SesionClase::getId);
    }

    private boolean exigeAulaEspecial(Grupo grupo) {
        return grupo.getAsignatura().getTipoAulaRequerido() != TipoAula.COMUN;
    }

    private boolean tieneDocenteConRestricciones(Grupo grupo) {
        return grupo.getDocente() != null && !grupo.getDocente().getRestricciones().isEmpty();
    }

    private IncidenciaDTO ubicar(SesionClase sesion, Horario horario, List<Aula> aulas,
            Map<DiaSemana, List<Integer>> iniciosPorDia, int duracion, long instanteLimite) {
        List<Aula> aulasCandidatas = seleccionarAulasCompatibles(sesion.getGrupo(), aulas);
        List<Long> sesionEvaluada = List.of(sesion.getId());
        IncidenciaDTO ultimaCausa = null;
        Integer diaConAdvertencia = null;
        Integer inicioConAdvertencia = null;
        Aula aulaConAdvertencia = null;
        boolean tiempoAgotado = false;
        recorrido:
        for (DiaSemana dia : DiaSemana.values()) {
            for (Integer inicio : iniciosPorDia.get(dia)) {
                for (Aula aula : aulasCandidatas) {
                    if (System.nanoTime() >= instanteLimite) {
                        tiempoAgotado = true;
                        break recorrido;
                    }
                    sesion.ubicar(dia.getNumero(), inicio, inicio + duracion, aula);
                    List<IncidenciaDTO> incidencias = validacionService.validarSesiones(horario, sesionEvaluada);
                    List<IncidenciaDTO> errores = validacionService.filtrarErrores(incidencias);
                    if (!errores.isEmpty()) {
                        ultimaCausa = errores.get(errores.size() - 1);
                        continue;
                    }
                    if (incidencias.isEmpty()) {
                        return null;
                    }
                    if (aulaConAdvertencia == null) {
                        diaConAdvertencia = dia.getNumero();
                        inicioConAdvertencia = inicio;
                        aulaConAdvertencia = aula;
                    }
                }
            }
        }
        if (aulaConAdvertencia != null) {
            sesion.ubicar(diaConAdvertencia, inicioConAdvertencia, inicioConAdvertencia + duracion,
                    aulaConAdvertencia);
            return null;
        }
        sesion.desubicar();
        if (tiempoAgotado) {
            return new IncidenciaDTO(null, SeveridadIncidencia.ERROR,
                    "La generación alcanzó el límite de " + limiteSegundos + " segundos antes de ubicar la sesión",
                    sesionEvaluada);
        }
        if (ultimaCausa == null) {
            return new IncidenciaDTO(null, SeveridadIncidencia.ERROR,
                    "No existen horas de inicio válidas con los parámetros de franja vigentes", sesionEvaluada);
        }
        return ultimaCausa;
    }

    private List<Aula> seleccionarAulasCompatibles(Grupo grupo, List<Aula> aulas) {
        List<Aula> compatibles = aulas.stream()
                .filter(aula -> aula.getTipo() == grupo.getAsignatura().getTipoAulaRequerido())
                .filter(aula -> aula.getCapacidad() >= grupo.getCupo())
                .toList();
        return compatibles.isEmpty() ? aulas : compatibles;
    }
}
