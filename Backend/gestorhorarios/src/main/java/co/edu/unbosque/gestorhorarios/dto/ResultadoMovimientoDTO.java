package co.edu.unbosque.gestorhorarios.dto;

import java.util.List;

public class ResultadoMovimientoDTO {

    private SesionClaseDTO sesion;
    private List<IncidenciaDTO> advertencias;

    public ResultadoMovimientoDTO() {
    }

    public ResultadoMovimientoDTO(SesionClaseDTO sesion, List<IncidenciaDTO> advertencias) {
        this.sesion = sesion;
        this.advertencias = advertencias;
    }

    public SesionClaseDTO getSesion() {
        return sesion;
    }

    public void setSesion(SesionClaseDTO sesion) {
        this.sesion = sesion;
    }

    public List<IncidenciaDTO> getAdvertencias() {
        return advertencias;
    }

    public void setAdvertencias(List<IncidenciaDTO> advertencias) {
        this.advertencias = advertencias;
    }
}
