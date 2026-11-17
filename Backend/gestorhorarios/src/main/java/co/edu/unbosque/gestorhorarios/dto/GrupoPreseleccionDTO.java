package co.edu.unbosque.gestorhorarios.dto;

import java.util.List;

/**
 * Grupo que el estudiante puede preseleccionar (RF-32): sus sesiones y solo el
 * nombre del docente, sin vinculación ni restricciones (RNF-07).
 */
public class GrupoPreseleccionDTO {

    private Long grupoId;
    private Integer numero;
    private Integer cupo;
    private String nombreDocente;
    private List<SesionClaseDTO> sesiones;

    public GrupoPreseleccionDTO() {
    }

    public GrupoPreseleccionDTO(Long grupoId, Integer numero, Integer cupo, String nombreDocente,
            List<SesionClaseDTO> sesiones) {
        this.grupoId = grupoId;
        this.numero = numero;
        this.cupo = cupo;
        this.nombreDocente = nombreDocente;
        this.sesiones = sesiones;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getCupo() {
        return cupo;
    }

    public void setCupo(Integer cupo) {
        this.cupo = cupo;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

    public List<SesionClaseDTO> getSesiones() {
        return sesiones;
    }

    public void setSesiones(List<SesionClaseDTO> sesiones) {
        this.sesiones = sesiones;
    }
}
