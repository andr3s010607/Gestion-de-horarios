package co.edu.unbosque.gestorhorarios.entity;

import co.edu.unbosque.gestorhorarios.enums.TipoAula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "asignatura")
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Min(1)
    @Max(4)
    @Column(name = "sesiones_semana", nullable = false)
    private Integer sesionesSemana;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_aula_requerido", nullable = false, length = 20)
    private TipoAula tipoAulaRequerido;

    public Asignatura() {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getSesionesSemana() {
        return sesionesSemana;
    }

    public void setSesionesSemana(Integer sesionesSemana) {
        this.sesionesSemana = sesionesSemana;
    }

    public TipoAula getTipoAulaRequerido() {
        return tipoAulaRequerido;
    }

    public void setTipoAulaRequerido(TipoAula tipoAulaRequerido) {
        this.tipoAulaRequerido = tipoAulaRequerido;
    }
}
