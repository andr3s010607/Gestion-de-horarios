package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

import java.time.LocalTime;

import co.edu.unbosque.gestorhorarios.enums.DiaSemana;

public class SesionClaseDTO {

    private Long id;
    private Long horarioId;
    private Long grupoId;
    private Long aulaId;
    private DiaSemana dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public SesionClaseDTO() {
    }

    public SesionClaseDTO(Long id, Long horarioId, Long grupoId, Long aulaId, DiaSemana dia,
            LocalTime horaInicio, LocalTime horaFin) {
        this.id = id;
        this.horarioId = horarioId;
        this.grupoId = grupoId;
        this.aulaId = aulaId;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getHorarioId() { return horarioId; }
    public void setHorarioId(Long horarioId) { this.horarioId = horarioId; }
    public Long getGrupoId() { return grupoId; }
    public void setGrupoId(Long grupoId) { this.grupoId = grupoId; }
    public Long getAulaId() { return aulaId; }
    public void setAulaId(Long aulaId) { this.aulaId = aulaId; }
    public DiaSemana getDia() { return dia; }
    public void setDia(DiaSemana dia) { this.dia = dia; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    @Override
    public int hashCode() {
        return Objects.hash(id, horarioId, grupoId, aulaId, dia, horaInicio, horaFin);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SesionClaseDTO other = (SesionClaseDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(horarioId, other.horarioId)
                && Objects.equals(grupoId, other.grupoId)
                && Objects.equals(aulaId, other.aulaId)
                && Objects.equals(dia, other.dia)
                && Objects.equals(horaInicio, other.horaInicio)
                && Objects.equals(horaFin, other.horaFin);
    }

    @Override
    public String toString() {
        return "SesionClaseDTO [id=" + id + ", horarioId=" + horarioId + ", grupoId=" + grupoId + ", aulaId=" + aulaId + ", dia=" + dia + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin + "]";
    }
}
