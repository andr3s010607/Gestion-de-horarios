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
@Table(name = "sesion")
public class SesionClase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "horario_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Horario horario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id")
    private Aula aula;

    @Min(1)
    @Max(6)
    private Integer dia;

    @Min(0)
    @Max(1439)
    @Column(name = "minuto_inicio")
    private Integer minutoInicio;

    @Min(1)
    @Max(1440)
    @Column(name = "minuto_fin")
    private Integer minutoFin;

    public SesionClase() {
    }

    public SesionClase(Grupo grupo) {
        this.grupo = grupo;
        this.horario = grupo.getHorario();
        this.docente = grupo.getDocente();
    }

    public boolean estaUbicada() {
        return dia != null && minutoInicio != null && minutoFin != null;
    }

    public void ubicar(Integer dia, Integer minutoInicio, Integer minutoFin, Aula aula) {
        this.dia = dia;
        this.minutoInicio = minutoInicio;
        this.minutoFin = minutoFin;
        this.aula = aula;
    }

    public void desubicar() {
        ubicar(null, null, null, null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
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

    public Horario getHorario() {
        return horario;
    }

    public Docente getDocente() {
        return docente;
    }
}
