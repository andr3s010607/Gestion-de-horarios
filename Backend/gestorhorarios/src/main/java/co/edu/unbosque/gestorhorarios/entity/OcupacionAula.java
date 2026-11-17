package co.edu.unbosque.gestorhorarios.entity;

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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(
        name = "ocupacion_aula",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ocupacion_aula",
                columnNames = {"horario_id", "aula_id", "dia", "bloque"}))
public class OcupacionAula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sesion_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SesionClase sesion;

    @Column(name = "horario_id", nullable = false)
    private Long horarioId;

    @Column(name = "aula_id", nullable = false)
    private Long aulaId;

    @Column(nullable = false)
    private Integer dia;

    @Column(nullable = false)
    private Integer bloque;

    public OcupacionAula() {
    }

    public OcupacionAula(SesionClase sesion, Integer bloque) {
        this.sesion = sesion;
        this.horarioId = sesion.getHorario().getId();
        this.aulaId = sesion.getAula().getId();
        this.dia = sesion.getDia();
        this.bloque = bloque;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SesionClase getSesion() {
        return sesion;
    }

    public void setSesion(SesionClase sesion) {
        this.sesion = sesion;
    }

    public Long getHorarioId() {
        return horarioId;
    }

    public void setHorarioId(Long horarioId) {
        this.horarioId = horarioId;
    }

    public Long getAulaId() {
        return aulaId;
    }

    public void setAulaId(Long aulaId) {
        this.aulaId = aulaId;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Integer getBloque() {
        return bloque;
    }

    public void setBloque(Integer bloque) {
        this.bloque = bloque;
    }

    public Long getId() {
        return id;
    }

    public Integer getDia() {
        return dia;
    }
}
