package co.edu.unbosque.gestorhorarios.dto;

import co.edu.unbosque.gestorhorarios.enums.SeveridadIncidencia;
import java.util.List;

public class IncidenciaDTO {

    private String codigo;
    private SeveridadIncidencia severidad;
    private String mensaje;
    private List<Long> sesionIds;

    public IncidenciaDTO() {
    }

    public IncidenciaDTO(String codigo, SeveridadIncidencia severidad, String mensaje, List<Long> sesionIds) {
        this.codigo = codigo;
        this.severidad = severidad;
        this.mensaje = mensaje;
        this.sesionIds = sesionIds;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public SeveridadIncidencia getSeveridad() {
        return severidad;
    }

    public void setSeveridad(SeveridadIncidencia severidad) {
        this.severidad = severidad;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<Long> getSesionIds() {
        return sesionIds;
    }

    public void setSesionIds(List<Long> sesionIds) {
        this.sesionIds = sesionIds;
    }
}
