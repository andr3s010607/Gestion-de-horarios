package co.edu.unbosque.gestorhorarios.dto;

/** Número de preselecciones confirmadas de un grupo frente a su cupo (RF-35). */
public class DemandaGrupoDTO {

    private Long grupoId;
    private Long asignaturaId;
    private String codigoAsignatura;
    private String nombreAsignatura;
    private Integer numero;
    private Integer cupo;
    private long preseleccionados;

    public DemandaGrupoDTO() {
    }

    public DemandaGrupoDTO(Long grupoId, Long asignaturaId, String codigoAsignatura, String nombreAsignatura,
            Integer numero, Integer cupo, long preseleccionados) {
        this.grupoId = grupoId;
        this.asignaturaId = asignaturaId;
        this.codigoAsignatura = codigoAsignatura;
        this.nombreAsignatura = nombreAsignatura;
        this.numero = numero;
        this.cupo = cupo;
        this.preseleccionados = preseleccionados;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public String getCodigoAsignatura() {
        return codigoAsignatura;
    }

    public void setCodigoAsignatura(String codigoAsignatura) {
        this.codigoAsignatura = codigoAsignatura;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
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

    public long getPreseleccionados() {
        return preseleccionados;
    }

    public void setPreseleccionados(long preseleccionados) {
        this.preseleccionados = preseleccionados;
    }
}
