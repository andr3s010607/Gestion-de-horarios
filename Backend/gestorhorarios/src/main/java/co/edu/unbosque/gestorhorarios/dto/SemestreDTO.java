package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

public class SemestreDTO {

    private Long id;
    private Integer anio;
    private Integer periodo;
    private boolean activo;

    public SemestreDTO() {
    }

    public SemestreDTO(Long id, Integer anio, Integer periodo, boolean activo) {
        this.id = id;
        this.anio = anio;
        this.periodo = periodo;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public Integer getPeriodo() { return periodo; }
    public void setPeriodo(Integer periodo) { this.periodo = periodo; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    @Override
    public int hashCode() {
        return Objects.hash(id, anio, periodo, activo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SemestreDTO other = (SemestreDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(anio, other.anio)
                && Objects.equals(periodo, other.periodo)
                && activo == other.activo;
    }

    @Override
    public String toString() {
        return "SemestreDTO [id=" + id + ", anio=" + anio + ", periodo=" + periodo + ", activo=" + activo + "]";
    }
}
