package co.edu.unbosque.gestorhorarios.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResultadoGeneracionHorarioDTO {

    private int sesionesCreadas;
    private List<String> mensajes;

    public ResultadoGeneracionHorarioDTO() { this.mensajes = new ArrayList<>(); }

    public ResultadoGeneracionHorarioDTO(int sesionesCreadas, List<String> mensajes) {
        this.sesionesCreadas = sesionesCreadas;
        this.mensajes = mensajes;
    }

    public int getSesionesCreadas() { return sesionesCreadas; }
    public void setSesionesCreadas(int sesionesCreadas) { this.sesionesCreadas = sesionesCreadas; }
    public List<String> getMensajes() { return mensajes; }
    public void setMensajes(List<String> mensajes) { this.mensajes = mensajes; }

    @Override
    public int hashCode() { return Objects.hash(sesionesCreadas, mensajes); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ResultadoGeneracionHorarioDTO other = (ResultadoGeneracionHorarioDTO) obj;
        return sesionesCreadas == other.sesionesCreadas && Objects.equals(mensajes, other.mensajes);
    }

    @Override
    public String toString() {
        return "ResultadoGeneracionHorarioDTO [sesionesCreadas=" + sesionesCreadas + ", mensajes=" + mensajes + "]";
    }
}
