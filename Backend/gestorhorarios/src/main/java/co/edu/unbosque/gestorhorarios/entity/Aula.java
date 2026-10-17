package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Espacio físico disponible para programar sesiones de clase. */
@Entity
@Table(name = "aulas")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(length = 100)
    private String ubicacion;

    @Column(nullable = false)
    private boolean activa = true;

    public Aula() {
    }

    public Aula(String codigo, String nombre, Integer capacidad, String ubicacion, boolean activa) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
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

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codigo, nombre, capacidad, ubicacion, activa);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Aula other = (Aula) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(codigo, other.codigo)
                && Objects.equals(nombre, other.nombre)
                && Objects.equals(capacidad, other.capacidad)
                && Objects.equals(ubicacion, other.ubicacion)
                && activa == other.activa;
    }

    @Override
    public String toString() {
        return "Aula [id=" + id + ", codigo=" + codigo + ", nombre=" + nombre + ", capacidad=" + capacidad + ", ubicacion=" + ubicacion + ", activa=" + activa + "]";
    }
}
