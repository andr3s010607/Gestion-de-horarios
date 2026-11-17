package co.edu.unbosque.gestorhorarios.dto;

import co.edu.unbosque.gestorhorarios.enums.RolUsuario;

public class RespuestaAutenticacionDTO {

    private String token;
    private String tipoToken;
    private Long id;
    private String nombreUsuario;
    private RolUsuario rol;
    private Long docenteId;

    public RespuestaAutenticacionDTO() {
    }

    public RespuestaAutenticacionDTO(String token, String tipoToken, Long id, String nombreUsuario, RolUsuario rol,
            Long docenteId) {
        this.token = token;
        this.tipoToken = tipoToken;
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
        this.docenteId = docenteId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
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
