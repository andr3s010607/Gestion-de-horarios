package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

public class AulaRecursoDTO {

    private Long id;
    private Long aulaId;
    private Long recursoId;

    public AulaRecursoDTO() {
    }

    public AulaRecursoDTO(Long id, Long aulaId, Long recursoId) {
        this.id = id;
        this.aulaId = aulaId;
        this.recursoId = recursoId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAulaId() { return aulaId; }
    public void setAulaId(Long aulaId) { this.aulaId = aulaId; }
    public Long getRecursoId() { return recursoId; }
    public void setRecursoId(Long recursoId) { this.recursoId = recursoId; }
    @Override
    public int hashCode() {
        return Objects.hash(id, aulaId, recursoId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AulaRecursoDTO other = (AulaRecursoDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(aulaId, other.aulaId)
                && Objects.equals(recursoId, other.recursoId);
    }

    @Override
    public String toString() {
        return "AulaRecursoDTO [id=" + id + ", aulaId=" + aulaId + ", recursoId=" + recursoId + "]";
    }
}
