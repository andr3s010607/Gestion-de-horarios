package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import java.time.LocalTime;

import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.TipoRestriccionHorario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Bloque semanal de disponibilidad o indisponibilidad declarado por un docente. */
@Entity
@Table(name = "restricciones_horarias_docente")
public class RestriccionHorariaDocente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private DiaSemana dia;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoRestriccionHorario tipo = TipoRestriccionHorario.NO_DISPONIBLE;

    public RestriccionHorariaDocente() {
    }

    public RestriccionHorariaDocente(Docente docente, DiaSemana dia, LocalTime horaInicio, LocalTime horaFin, TipoRestriccionHorario tipo) {
        this.docente = docente;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tipo = tipo;
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

    public DiaSemana getDia() {
        return dia;
    }

    public void setDia(DiaSemana dia) {
        this.dia = dia;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public TipoRestriccionHorario getTipo() {
        return tipo;
    }

    public void setTipo(TipoRestriccionHorario tipo) {
        this.tipo = tipo;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, docente, dia, horaInicio, horaFin, tipo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RestriccionHorariaDocente other = (RestriccionHorariaDocente) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(docente, other.docente)
                && Objects.equals(dia, other.dia)
                && Objects.equals(horaInicio, other.horaInicio)
                && Objects.equals(horaFin, other.horaFin)
                && Objects.equals(tipo, other.tipo);
    }

    @Override
    public String toString() {
        return "RestriccionHorariaDocente [id=" + id + ", docente=" + docente + ", dia=" + dia + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin + ", tipo=" + tipo + "]";
    }
}
