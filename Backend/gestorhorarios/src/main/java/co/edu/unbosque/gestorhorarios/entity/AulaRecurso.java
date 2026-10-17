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

/** Relaciona un aula con uno de los recursos físicos que posee. */
@Entity
@Table(
        name = "aula_recursos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"aula_id", "recurso_id"}))
public class AulaRecurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recurso_id", nullable = false)
    private RecursoAula recurso;

    public AulaRecurso() {
    }

    public AulaRecurso(Aula aula, RecursoAula recurso) {
        this.aula = aula;
        this.recurso = recurso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public RecursoAula getRecurso() {
        return recurso;
    }

    public void setRecurso(RecursoAula recurso) {
        this.recurso = recurso;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, aula, recurso);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AulaRecurso other = (AulaRecurso) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(aula, other.aula)
                && Objects.equals(recurso, other.recurso);
    }

    @Override
    public String toString() {
        return "AulaRecurso [id=" + id + ", aula=" + aula + ", recurso=" + recurso + "]";
    }
}
