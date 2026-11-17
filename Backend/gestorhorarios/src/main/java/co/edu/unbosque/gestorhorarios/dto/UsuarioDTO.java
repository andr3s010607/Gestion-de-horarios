package co.edu.unbosque.gestorhorarios.dto;

import co.edu.unbosque.gestorhorarios.enums.RolUsuario;

public class UsuarioDTO {

    private Long id;
    private String nombreUsuario;
    private String contrasena;
    private RolUsuario rol;
    private Long docenteId;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String nombreUsuario, String contrasena, RolUsuario rol, Long docenteId) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.docenteId = docenteId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }
}
