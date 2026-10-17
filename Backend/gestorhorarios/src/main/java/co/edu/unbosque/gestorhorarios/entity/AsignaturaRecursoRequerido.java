package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Relaciona una asignatura con el recurso que necesita para poder programarse. */
@Entity
@Table(
        name = "asignatura_recursos_requeridos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"asignatura_id", "recurso_id"}))
public class AsignaturaRecursoRequerido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recurso_id", nullable = false)
    private RecursoAula recurso;

    public AsignaturaRecursoRequerido() {
    }

    public AsignaturaRecursoRequerido(Asignatura asignatura, RecursoAula recurso) {
        this.asignatura = asignatura;
        this.recurso = recurso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public RecursoAula getRecurso() {
        return recurso;
    }

    public void setRecurso(RecursoAula recurso) {
        this.recurso = recurso;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, asignatura, recurso);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AsignaturaRecursoRequerido other = (AsignaturaRecursoRequerido) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(asignatura, other.asignatura)
                && Objects.equals(recurso, other.recurso);
    }

    @Override
    public String toString() {
        return "AsignaturaRecursoRequerido [id=" + id + ", asignatura=" + asignatura + ", recurso=" + recurso + "]";
    }
}
