package co.edu.unbosque.gestorhorarios.entity;

import java.util.Objects;

import java.time.LocalTime;

import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
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

/** Bloque semanal recurrente asignado a un grupo dentro de una propuesta de horario. */
@Entity
@Table(name = "sesiones_clase")
public class SesionClase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private DiaSemana dia;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    public SesionClase() {
    }

    public SesionClase(Horario horario, Grupo grupo, Aula aula, DiaSemana dia, LocalTime horaInicio, LocalTime horaFin) {
        this.horario = horario;
        this.grupo = grupo;
        this.aula = aula;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
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
    @Override
    public int hashCode() {
        return Objects.hash(id, horario, grupo, aula, dia, horaInicio, horaFin);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SesionClase other = (SesionClase) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(horario, other.horario)
                && Objects.equals(grupo, other.grupo)
                && Objects.equals(aula, other.aula)
                && Objects.equals(dia, other.dia)
                && Objects.equals(horaInicio, other.horaInicio)
                && Objects.equals(horaFin, other.horaFin);
    }

    @Override
    public String toString() {
        return "SesionClase [id=" + id + ", horario=" + horario + ", grupo=" + grupo + ", aula=" + aula + ", dia=" + dia + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin + "]";
    }
}
