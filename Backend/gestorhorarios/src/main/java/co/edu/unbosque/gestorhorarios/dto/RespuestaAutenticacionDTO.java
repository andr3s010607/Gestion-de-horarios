package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

import co.edu.unbosque.gestorhorarios.enums.RolUsuario;

public class RespuestaAutenticacionDTO {

    private String token;
    private String tipoToken;
    private Long id;
    private String correo;
    private RolUsuario rol;

    public RespuestaAutenticacionDTO() {
    }

    public RespuestaAutenticacionDTO(String token, String tipoToken, Long id, String correo, RolUsuario rol) {
        this.token = token;
        this.tipoToken = tipoToken;
        this.id = id;
        this.correo = correo;
        this.rol = rol;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, tipoToken, id, correo, rol);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RespuestaAutenticacionDTO other = (RespuestaAutenticacionDTO) obj;
        return Objects.equals(token, other.token) && Objects.equals(tipoToken, other.tipoToken)
                && Objects.equals(id, other.id) && Objects.equals(correo, other.correo)
                && rol == other.rol;
    }

    @Override
    public String toString() {
        return "RespuestaAutenticacionDTO [token=" + token + ", tipoToken=" + tipoToken + ", id=" + id
                + ", correo=" + correo + ", rol=" + rol + "]";
    }
}
