package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

public class LoginDTO {

    private String correo;
    private String contrasena;

    public LoginDTO() {
    }

    public LoginDTO(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    @Override
    public int hashCode() {
        return Objects.hash(correo, contrasena);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LoginDTO other = (LoginDTO) obj;
        return Objects.equals(correo, other.correo) && Objects.equals(contrasena, other.contrasena);
    }

    @Override
    public String toString() {
        return "LoginDTO [correo=" + correo + ", contrasena=" + contrasena + "]";
    }
}
