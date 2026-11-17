package co.edu.unbosque.gestorhorarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DocenteAsignaturaId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "docente_id")
    private Long docenteId;

    @Column(name = "asignatura_id")
    private Long asignaturaId;

    public DocenteAsignaturaId() {
    }

    public DocenteAsignaturaId(Long docenteId, Long asignaturaId) {
        this.docenteId = docenteId;
        this.asignaturaId = asignaturaId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(docenteId, asignaturaId);
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (otro == null || getClass() != otro.getClass()) {
            return false;
        }
        DocenteAsignaturaId otroId = (DocenteAsignaturaId) otro;
        return Objects.equals(docenteId, otroId.docenteId) && Objects.equals(asignaturaId, otroId.asignaturaId);
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }
}
