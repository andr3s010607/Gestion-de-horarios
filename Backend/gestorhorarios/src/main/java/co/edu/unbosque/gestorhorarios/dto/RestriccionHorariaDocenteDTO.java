package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

import java.time.LocalTime;

import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.TipoRestriccionHorario;

public class RestriccionHorariaDocenteDTO {

    private Long id;
    private Long docenteId;
    private DiaSemana dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private TipoRestriccionHorario tipo;

    public RestriccionHorariaDocenteDTO() {
    }

    public RestriccionHorariaDocenteDTO(Long id, Long docenteId, DiaSemana dia, LocalTime horaInicio,
            LocalTime horaFin, TipoRestriccionHorario tipo) {
        this.id = id;
        this.docenteId = docenteId;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDocenteId() { return docenteId; }
    public void setDocenteId(Long docenteId) { this.docenteId = docenteId; }
    public DiaSemana getDia() { return dia; }
    public void setDia(DiaSemana dia) { this.dia = dia; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public TipoRestriccionHorario getTipo() { return tipo; }
    public void setTipo(TipoRestriccionHorario tipo) { this.tipo = tipo; }
    @Override
    public int hashCode() {
        return Objects.hash(id, docenteId, dia, horaInicio, horaFin, tipo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RestriccionHorariaDocenteDTO other = (RestriccionHorariaDocenteDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(docenteId, other.docenteId)
                && Objects.equals(dia, other.dia)
                && Objects.equals(horaInicio, other.horaInicio)
                && Objects.equals(horaFin, other.horaFin)
                && Objects.equals(tipo, other.tipo);
    }

    @Override
    public String toString() {
        return "RestriccionHorariaDocenteDTO [id=" + id + ", docenteId=" + docenteId + ", dia=" + dia + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin + ", tipo=" + tipo + "]";
    }
}
