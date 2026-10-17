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

/** Habilitación de un docente para dictar una asignatura. */
@Entity
@Table(
        name = "docente_asignaturas",
        uniqueConstraints = @UniqueConstraint(columnNames = {"docente_id", "asignatura_id"}))
public class DocenteAsignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    public DocenteAsignatura() {
    }

    public DocenteAsignatura(Docente docente, Asignatura asignatura) {
        this.docente = docente;
        this.asignatura = asignatura;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, docente, asignatura);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DocenteAsignatura other = (DocenteAsignatura) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(docente, other.docente)
                && Objects.equals(asignatura, other.asignatura);
    }

    @Override
    public String toString() {
        return "DocenteAsignatura [id=" + id + ", docente=" + docente + ", asignatura=" + asignatura + "]";
    }
}
