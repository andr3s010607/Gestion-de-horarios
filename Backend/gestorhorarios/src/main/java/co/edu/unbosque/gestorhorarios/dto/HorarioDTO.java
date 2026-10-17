package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

import java.time.LocalDateTime;

import co.edu.unbosque.gestorhorarios.enums.EstadoHorario;

public class HorarioDTO {

    private Long id;
    private String nombre;
    private Long semestreId;
    private EstadoHorario estado;
    private LocalDateTime fechaCreacion;

    public HorarioDTO() {
    }

    public HorarioDTO(Long id, String nombre, Long semestreId, EstadoHorario estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.semestreId = semestreId;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Long getSemestreId() { return semestreId; }
    public void setSemestreId(Long semestreId) { this.semestreId = semestreId; }
    public EstadoHorario getEstado() { return estado; }
    public void setEstado(EstadoHorario estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, semestreId, estado, fechaCreacion);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HorarioDTO other = (HorarioDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(nombre, other.nombre)
                && Objects.equals(semestreId, other.semestreId)
                && Objects.equals(estado, other.estado)
                && Objects.equals(fechaCreacion, other.fechaCreacion);
    }

    @Override
    public String toString() {
        return "HorarioDTO [id=" + id + ", nombre=" + nombre + ", semestreId=" + semestreId + ", estado=" + estado + ", fechaCreacion=" + fechaCreacion + "]";
    }
}
