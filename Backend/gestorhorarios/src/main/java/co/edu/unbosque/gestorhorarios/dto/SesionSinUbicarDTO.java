package co.edu.unbosque.gestorhorarios.dto;

public class SesionSinUbicarDTO {

    private SesionClaseDTO sesion;
    private String codigoCausa;
    private String causa;

    public SesionSinUbicarDTO() {
    }

    public SesionSinUbicarDTO(SesionClaseDTO sesion, String codigoCausa, String causa) {
        this.sesion = sesion;
        this.codigoCausa = codigoCausa;
        this.causa = causa;
    }

    public SesionClaseDTO getSesion() {
        return sesion;
    }

    public void setSesion(SesionClaseDTO sesion) {
        this.sesion = sesion;
    }

    public String getCodigoCausa() {
        return codigoCausa;
    }

    public void setCodigoCausa(String codigoCausa) {
        this.codigoCausa = codigoCausa;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }
}
