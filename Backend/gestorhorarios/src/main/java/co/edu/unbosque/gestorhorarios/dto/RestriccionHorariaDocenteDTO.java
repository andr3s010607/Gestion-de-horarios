package co.edu.unbosque.gestorhorarios.dto;

import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import java.time.LocalTime;

public class RestriccionHorariaDocenteDTO {

    private Long id;
    private Long docenteId;
    private DiaSemana dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public RestriccionHorariaDocenteDTO() {
    }

    public RestriccionHorariaDocenteDTO(Long id, Long docenteId, DiaSemana dia, LocalTime horaInicio,
            LocalTime horaFin) {
        this.id = id;
        this.docenteId = docenteId;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
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

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}
