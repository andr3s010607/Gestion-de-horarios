package co.edu.unbosque.gestorhorarios.dto;

import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import java.time.LocalTime;

public class MovimientoSesionDTO {

    private DiaSemana dia;
    private LocalTime horaInicio;
    private Long aulaId;

    public MovimientoSesionDTO() {
    }

    public MovimientoSesionDTO(DiaSemana dia, LocalTime horaInicio, Long aulaId) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.aulaId = aulaId;
    }

    public DiaSemana getDia() {
        return dia;
    }

    public void setDia(DiaSemana dia) {
        this.dia = dia;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Long getAulaId() {
        return aulaId;
    }

    public void setAulaId(Long aulaId) {
        this.aulaId = aulaId;
    }
}
