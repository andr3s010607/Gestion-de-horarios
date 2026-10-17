package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import co.edu.unbosque.gestorhorarios.enums.TipoVinculacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Docente disponible para dictar asignaturas durante la planeación. */
@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombres;

    @Column(nullable = false, length = 80)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoVinculacion tipoVinculacion;

    @Column(nullable = false)
    private boolean activo = true;

    public Docente() {
    }

    public Docente(String nombres, String apellidos, String correo, TipoVinculacion tipoVinculacion, boolean activo) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.tipoVinculacion = tipoVinculacion;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public TipoVinculacion getTipoVinculacion() {
        return tipoVinculacion;
    }

    public void setTipoVinculacion(TipoVinculacion tipoVinculacion) {
        this.tipoVinculacion = tipoVinculacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombres, apellidos, correo, tipoVinculacion, activo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Docente other = (Docente) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(nombres, other.nombres)
                && Objects.equals(apellidos, other.apellidos)
                && Objects.equals(correo, other.correo)
                && Objects.equals(tipoVinculacion, other.tipoVinculacion)
                && activo == other.activo;
    }

    @Override
    public String toString() {
        return "Docente [id=" + id + ", nombres=" + nombres + ", apellidos=" + apellidos + ", correo=" + correo + ", tipoVinculacion=" + tipoVinculacion + ", activo=" + activo + "]";
    }
}
