package co.edu.unbosque.gestorhorarios.dto;

import java.util.Objects;

public class DocenteAsignaturaDTO {

    private Long id;
    private Long docenteId;
    private Long asignaturaId;

    public DocenteAsignaturaDTO() {
    }

    public DocenteAsignaturaDTO(Long id, Long docenteId, Long asignaturaId) {
        this.id = id;
        this.docenteId = docenteId;
        this.asignaturaId = asignaturaId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDocenteId() { return docenteId; }
    public void setDocenteId(Long docenteId) { this.docenteId = docenteId; }
    public Long getAsignaturaId() { return asignaturaId; }
    public void setAsignaturaId(Long asignaturaId) { this.asignaturaId = asignaturaId; }
    @Override
    public int hashCode() {
        return Objects.hash(id, docenteId, asignaturaId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DocenteAsignaturaDTO other = (DocenteAsignaturaDTO) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(docenteId, other.docenteId)
                && Objects.equals(asignaturaId, other.asignaturaId);
    }

    @Override
    public String toString() {
        return "DocenteAsignaturaDTO [id=" + id + ", docenteId=" + docenteId + ", asignaturaId=" + asignaturaId + "]";
    }
}
