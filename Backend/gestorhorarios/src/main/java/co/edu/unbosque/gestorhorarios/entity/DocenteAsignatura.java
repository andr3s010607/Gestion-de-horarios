package co.edu.unbosque.gestorhorarios.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "docente_asignatura")
public class DocenteAsignatura {

    @EmbeddedId
    private DocenteAsignaturaId id;

    @MapsId("docenteId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @MapsId("asignaturaId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    public DocenteAsignatura() {
    }

    public DocenteAsignatura(Docente docente, Asignatura asignatura) {
        this.id = new DocenteAsignaturaId(docente.getId(), asignatura.getId());
        this.docente = docente;
        this.asignatura = asignatura;
    }

    public void setId(DocenteAsignaturaId id) {
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

    public DocenteAsignaturaId getId() {
        return id;
    }
}
