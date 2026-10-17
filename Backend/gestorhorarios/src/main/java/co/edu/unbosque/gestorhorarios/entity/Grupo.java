package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Oferta de una asignatura para un semestre y docente determinados. */
@Entity
@Table(
        name = "grupos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"semestre_id", "codigo"}))
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false)
    private Integer cupoPlaneado;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    public Grupo() {
    }

    public Grupo(String codigo, Integer cupoPlaneado, boolean activo, Asignatura asignatura, Semestre semestre, Docente docente) {
        this.codigo = codigo;
        this.cupoPlaneado = cupoPlaneado;
        this.activo = activo;
        this.asignatura = asignatura;
        this.semestre = semestre;
        this.docente = docente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCupoPlaneado() {
        return cupoPlaneado;
    }

    public void setCupoPlaneado(Integer cupoPlaneado) {
        this.cupoPlaneado = cupoPlaneado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, codigo, cupoPlaneado, activo, asignatura, semestre, docente);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Grupo other = (Grupo) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(codigo, other.codigo)
                && Objects.equals(cupoPlaneado, other.cupoPlaneado)
                && activo == other.activo
                && Objects.equals(asignatura, other.asignatura)
                && Objects.equals(semestre, other.semestre)
                && Objects.equals(docente, other.docente);
    }

    @Override
    public String toString() {
        return "Grupo [id=" + id + ", codigo=" + codigo + ", cupoPlaneado=" + cupoPlaneado + ", activo=" + activo + ", asignatura=" + asignatura + ", semestre=" + semestre + ", docente=" + docente + "]";
    }
}
