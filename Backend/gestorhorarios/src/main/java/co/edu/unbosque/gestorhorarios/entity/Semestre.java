package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Periodo académico al que pertenece un horario. */
@Entity
@Table(name = "semestres", uniqueConstraints = @UniqueConstraint(columnNames = {"anio", "periodo"}))
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Integer periodo;

    @Column(nullable = false)
    private boolean activo = true;

    public Semestre() {
    }

    public Semestre(Integer anio, Integer periodo, boolean activo) {
        this.anio = anio;
        this.periodo = periodo;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, anio, periodo, activo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Semestre other = (Semestre) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(anio, other.anio)
                && Objects.equals(periodo, other.periodo)
                && activo == other.activo;
    }

    @Override
    public String toString() {
        return "Semestre [id=" + id + ", anio=" + anio + ", periodo=" + periodo + ", activo=" + activo + "]";
    }
}
