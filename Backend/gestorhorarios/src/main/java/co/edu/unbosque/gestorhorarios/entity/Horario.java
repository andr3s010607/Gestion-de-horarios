package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import java.time.LocalDateTime;

import co.edu.unbosque.gestorhorarios.enums.EstadoHorario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Propuesta de horario creada para un semestre. */
@Entity
@Table(name = "horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoHorario estado = EstadoHorario.BORRADOR;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public Horario() {
    }

    public Horario(String nombre, Semestre semestre, EstadoHorario estado, LocalDateTime fechaCreacion) {
        this.nombre = nombre;
        this.semestre = semestre;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    public EstadoHorario getEstado() {
        return estado;
    }

    public void setEstado(EstadoHorario estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, semestre, estado, fechaCreacion);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Horario other = (Horario) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(nombre, other.nombre)
                && Objects.equals(semestre, other.semestre)
                && Objects.equals(estado, other.estado)
                && Objects.equals(fechaCreacion, other.fechaCreacion);
    }

    @Override
    public String toString() {
        return "Horario [id=" + id + ", nombre=" + nombre + ", semestre=" + semestre + ", estado=" + estado + ", fechaCreacion=" + fechaCreacion + "]";
    }
}
