package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

public class GrupoDTO {

    private Long id;
    private String codigo;
    private Integer cupoPlaneado;
    private boolean activo;
    private Long asignaturaId;
    private Long semestreId;
    private Long docenteId;

    public GrupoDTO() {
    }

    public GrupoDTO(Long id, String codigo, Integer cupoPlaneado, boolean activo, Long asignaturaId,
            Long semestreId, Long docenteId) {
        this.id = id;
        this.codigo = codigo;
        this.cupoPlaneado = cupoPlaneado;
        this.activo = activo;
        this.asignaturaId = asignaturaId;
        this.semestreId = semestreId;
        this.docenteId = docenteId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public Integer getCupoPlaneado() { return cupoPlaneado; }
    public void setCupoPlaneado(Integer cupoPlaneado) { this.cupoPlaneado = cupoPlaneado; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public Long getAsignaturaId() { return asignaturaId; }
    public void setAsignaturaId(Long asignaturaId) { this.asignaturaId = asignaturaId; }
    public Long getSemestreId() { return semestreId; }
    public void setSemestreId(Long semestreId) { this.semestreId = semestreId; }
    public Long getDocenteId() { return docenteId; }
    public void setDocenteId(Long docenteId) { this.docenteId = docenteId; }
    @Override
    public int hashCode() {
        return Objects.hash(id, codigo, cupoPlaneado, activo, asignaturaId, semestreId, docenteId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GrupoDTO other = (GrupoDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(codigo, other.codigo)
                && Objects.equals(cupoPlaneado, other.cupoPlaneado)
                && activo == other.activo
                && Objects.equals(asignaturaId, other.asignaturaId)
                && Objects.equals(semestreId, other.semestreId)
                && Objects.equals(docenteId, other.docenteId);
    }

    @Override
    public String toString() {
        return "GrupoDTO [id=" + id + ", codigo=" + codigo + ", cupoPlaneado=" + cupoPlaneado + ", activo=" + activo + ", asignaturaId=" + asignaturaId + ", semestreId=" + semestreId + ", docenteId=" + docenteId + "]";
    }
}
