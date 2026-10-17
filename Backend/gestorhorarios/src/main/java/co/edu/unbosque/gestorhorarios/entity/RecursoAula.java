package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Recurso físico que puede ser requerido por una asignatura u ofrecido por un aula. */
@Entity
@Table(name = "recursos_aula")
public class RecursoAula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(length = 250)
    private String descripcion;

    public RecursoAula() {
    }

    public RecursoAula(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, descripcion);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RecursoAula other = (RecursoAula) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(nombre, other.nombre)
                && Objects.equals(descripcion, other.descripcion);
    }

    @Override
    public String toString() {
        return "RecursoAula [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + "]";
    }
}
