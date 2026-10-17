package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

import co.edu.unbosque.gestorhorarios.enums.RolUsuario;

public class UsuarioDTO {

    private Long id;
    private String nombreCompleto;
    private String correo;
    private String contrasena;
    private RolUsuario rol;
    private boolean activo;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String nombreCompleto, String correo, String contrasena,
            RolUsuario rol, boolean activo) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    @Override
    public int hashCode() {
        return Objects.hash(id, nombreCompleto, correo, contrasena, rol, activo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UsuarioDTO other = (UsuarioDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(nombreCompleto, other.nombreCompleto)
                && Objects.equals(correo, other.correo)
                && Objects.equals(contrasena, other.contrasena)
                && Objects.equals(rol, other.rol)
                && activo == other.activo;
    }

    @Override
    public String toString() {
        return "UsuarioDTO [id=" + id + ", nombreCompleto=" + nombreCompleto + ", correo=" + correo
                + ", rol=" + rol + ", activo=" + activo + "]";
    }
}
