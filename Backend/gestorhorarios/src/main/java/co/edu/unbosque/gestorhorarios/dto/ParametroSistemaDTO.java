package co.edu.unbosque.gestorhorarios.dto;

import java.time.LocalTime;

public class ParametroSistemaDTO {

    private LocalTime horaInicioLunesViernes;
    private LocalTime horaFinLunesViernes;
    private LocalTime horaFinSabado;
    private LocalTime horaInicioPausa;
    private LocalTime horaFinPausa;
    private Integer duracionSesionHoras;
    private Integer cupoMaximo;
    private Integer toleranciaCupoPorcentaje;

    public ParametroSistemaDTO() {
    }

    public LocalTime getHoraInicioLunesViernes() {
        return horaInicioLunesViernes;
    }

    public void setHoraInicioLunesViernes(LocalTime horaInicioLunesViernes) {
        this.horaInicioLunesViernes = horaInicioLunesViernes;
    }

    public LocalTime getHoraFinLunesViernes() {
        return horaFinLunesViernes;
    }

    public void setHoraFinLunesViernes(LocalTime horaFinLunesViernes) {
        this.horaFinLunesViernes = horaFinLunesViernes;
    }

    public LocalTime getHoraFinSabado() {
        return horaFinSabado;
    }

    public void setHoraFinSabado(LocalTime horaFinSabado) {
        this.horaFinSabado = horaFinSabado;
    }

    public LocalTime getHoraInicioPausa() {
        return horaInicioPausa;
    }

    public void setHoraInicioPausa(LocalTime horaInicioPausa) {
        this.horaInicioPausa = horaInicioPausa;
    }

    public LocalTime getHoraFinPausa() {
        return horaFinPausa;
    }

    public void setHoraFinPausa(LocalTime horaFinPausa) {
        this.horaFinPausa = horaFinPausa;
    }

    public Integer getDuracionSesionHoras() {
        return duracionSesionHoras;
    }

    public void setDuracionSesionHoras(Integer duracionSesionHoras) {
        this.duracionSesionHoras = duracionSesionHoras;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public Integer getToleranciaCupoPorcentaje() {
        return toleranciaCupoPorcentaje;
    }

    public void setToleranciaCupoPorcentaje(Integer toleranciaCupoPorcentaje) {
        this.toleranciaCupoPorcentaje = toleranciaCupoPorcentaje;
    }
}
