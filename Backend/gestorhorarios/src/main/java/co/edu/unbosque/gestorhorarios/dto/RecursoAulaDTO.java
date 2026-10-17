package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

public class RecursoAulaDTO {

    private Long id;
    private String nombre;
    private String descripcion;

    public RecursoAulaDTO() {
    }

    public RecursoAulaDTO(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
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
        RecursoAulaDTO other = (RecursoAulaDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(nombre, other.nombre)
                && Objects.equals(descripcion, other.descripcion);
    }

    @Override
    public String toString() {
        return "RecursoAulaDTO [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + "]";
    }
}
