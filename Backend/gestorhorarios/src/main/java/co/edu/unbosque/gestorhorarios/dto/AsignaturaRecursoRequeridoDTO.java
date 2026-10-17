package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

public class AsignaturaRecursoRequeridoDTO {

    private Long id;
    private Long asignaturaId;
    private Long recursoId;

    public AsignaturaRecursoRequeridoDTO() {
    }

    public AsignaturaRecursoRequeridoDTO(Long id, Long asignaturaId, Long recursoId) {
        this.id = id;
        this.asignaturaId = asignaturaId;
        this.recursoId = recursoId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAsignaturaId() { return asignaturaId; }
    public void setAsignaturaId(Long asignaturaId) { this.asignaturaId = asignaturaId; }
    public Long getRecursoId() { return recursoId; }
    public void setRecursoId(Long recursoId) { this.recursoId = recursoId; }
    @Override
    public int hashCode() {
        return Objects.hash(id, asignaturaId, recursoId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AsignaturaRecursoRequeridoDTO other = (AsignaturaRecursoRequeridoDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(asignaturaId, other.asignaturaId)
                && Objects.equals(recursoId, other.recursoId);
    }

    @Override
    public String toString() {
        return "AsignaturaRecursoRequeridoDTO [id=" + id + ", asignaturaId=" + asignaturaId + ", recursoId=" + recursoId + "]";
    }
}
