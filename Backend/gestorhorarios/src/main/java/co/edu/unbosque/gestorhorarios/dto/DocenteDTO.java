package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

import co.edu.unbosque.gestorhorarios.enums.TipoVinculacion;

public class DocenteDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String correo;
    private TipoVinculacion tipoVinculacion;
    private boolean activo;

    public DocenteDTO() {
    }

    public DocenteDTO(Long id, String nombres, String apellidos, String correo,
            TipoVinculacion tipoVinculacion, boolean activo) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.tipoVinculacion = tipoVinculacion;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public TipoVinculacion getTipoVinculacion() { return tipoVinculacion; }
    public void setTipoVinculacion(TipoVinculacion tipoVinculacion) { this.tipoVinculacion = tipoVinculacion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
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
        DocenteDTO other = (DocenteDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(nombres, other.nombres)
                && Objects.equals(apellidos, other.apellidos)
                && Objects.equals(correo, other.correo)
                && Objects.equals(tipoVinculacion, other.tipoVinculacion)
                && activo == other.activo;
    }

    @Override
    public String toString() {
        return "DocenteDTO [id=" + id + ", nombres=" + nombres + ", apellidos=" + apellidos + ", correo=" + correo + ", tipoVinculacion=" + tipoVinculacion + ", activo=" + activo + "]";
    }
}
