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
public class ValidadorFranja implements Validador {

    public static final String FUERA_DE_FRANJA = "FRA-INI";
    public static final String CRUCE_CON_PAUSA = "FRA-PAU";

    private final ReglaHorarioService reglaHorarioService;

    public ValidadorFranja(ReglaHorarioService reglaHorarioService) {
        this.reglaHorarioService = reglaHorarioService;
    }

    @Override
    public List<IncidenciaDTO> validar(Horario horario) {
        int inicioFranja = reglaHorarioService.minutoInicioFranja();
        int inicioPausa = reglaHorarioService.minutoInicioPausa();
        int finPausa = reglaHorarioService.minutoFinPausa();
        int finFranjaLunesViernes = reglaHorarioService.minutoFinFranja(DiaSemana.LUNES.getNumero());
        int finFranjaSabado = reglaHorarioService.minutoFinFranja(DiaSemana.SABADO.getNumero());
        List<IncidenciaDTO> incidencias = new ArrayList<>();
        for (Grupo grupo : horario.getGrupos()) {
            for (SesionClase sesion : grupo.getSesiones()) {
                if (!sesion.estaUbicada()) {
                    continue;
                }
                int finFranja = sesion.getDia() == DiaSemana.SABADO.getNumero()
                        ? finFranjaSabado
                        : finFranjaLunesViernes;
                boolean iniciaAntes = sesion.getMinutoInicio() < inicioFranja;
                boolean terminaDespues = sesion.getMinutoFin() > finFranja;
                boolean iniciaFueraDeHoraValida = (sesion.getMinutoInicio() - inicioFranja)
                        % ReglaHorarioService.MINUTOS_POR_HORA != 0;
                if (iniciaAntes || terminaDespues || iniciaFueraDeHoraValida) {
                    incidencias.add(new IncidenciaDTO(FUERA_DE_FRANJA, SeveridadIncidencia.ERROR,
                            "La sesión de " + describir(sesion)
                                    + " no comienza en una hora válida de la franja permitida del día",
                            List.of(sesion.getId())));
                }
                if (ReglaHorarioService.seSolapan(sesion.getMinutoInicio(), sesion.getMinutoFin(), inicioPausa,
                        finPausa)) {
                    incidencias.add(new IncidenciaDTO(CRUCE_CON_PAUSA, SeveridadIncidencia.ERROR,
                            "La sesión de " + describir(sesion) + " cruza la pausa del mediodía",
                            List.of(sesion.getId())));
                }
            }
        }
        return incidencias;
    }

    private String describir(SesionClase sesion) {
        return sesion.getGrupo().getAsignatura().getCodigo() + " grupo " + sesion.getGrupo().getNumero();
    }
}
