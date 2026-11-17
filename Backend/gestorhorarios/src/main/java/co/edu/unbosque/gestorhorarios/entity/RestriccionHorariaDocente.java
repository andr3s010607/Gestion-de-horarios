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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "restriccion_horario")
public class RestriccionHorariaDocente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Docente docente;

    @Min(1)
    @Max(6)
    @Column(nullable = false)
    private Integer dia;

    @Min(0)
    @Max(1439)
    @Column(name = "minuto_inicio", nullable = false)
    private Integer minutoInicio;

    @Min(1)
    @Max(1440)
    @Column(name = "minuto_fin", nullable = false)
    private Integer minutoFin;

    public RestriccionHorariaDocente() {
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

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Integer getMinutoInicio() {
        return minutoInicio;
    }

    public void setMinutoInicio(Integer minutoInicio) {
        this.minutoInicio = minutoInicio;
    }

    public Integer getMinutoFin() {
        return minutoFin;
    }

    public void setMinutoFin(Integer minutoFin) {
        this.minutoFin = minutoFin;
    }
}
