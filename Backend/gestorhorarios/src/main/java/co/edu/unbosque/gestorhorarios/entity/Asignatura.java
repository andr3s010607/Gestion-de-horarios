package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Curso que debe ser programado dentro de un semestre. */
@Entity
@Table(name = "asignaturas")
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private Integer sesionesSemanales;

    @Column(nullable = false)
    private boolean activa = true;

    public Asignatura() {
    }

    public Asignatura(String codigo, String nombre, Integer sesionesSemanales, boolean activa) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.sesionesSemanales = sesionesSemanales;
        this.activa = activa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getSesionesSemanales() {
        return sesionesSemanales;
    }

    public void setSesionesSemanales(Integer sesionesSemanales) {
        this.sesionesSemanales = sesionesSemanales;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codigo, nombre, sesionesSemanales, activa);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Asignatura other = (Asignatura) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(codigo, other.codigo)
                && Objects.equals(nombre, other.nombre)
                && Objects.equals(sesionesSemanales, other.sesionesSemanales)
                && activa == other.activa;
    }

    @Override
    public String toString() {
        return "Asignatura [id=" + id + ", codigo=" + codigo + ", nombre=" + nombre + ", sesionesSemanales=" + sesionesSemanales + ", activa=" + activa + "]";
    }
}
