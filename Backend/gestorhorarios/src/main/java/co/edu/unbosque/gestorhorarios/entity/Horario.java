package co.edu.unbosque.gestorhorarios.entity;

import co.edu.unbosque.gestorhorarios.enums.EstadoHorario;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "horario")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private String semestre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoHorario estado = EstadoHorario.BORRADOR;

    @Convert(converter = ConvertidorSiNo.class)
    @Column(name = "preseleccion_abierta", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private Boolean preseleccionAbierta = false;

    @OneToMany(mappedBy = "horario")
    private List<Grupo> grupos = new ArrayList<>();

    public Horario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public EstadoHorario getEstado() {
        return estado;
    }

    public void setEstado(EstadoHorario estado) {
        this.estado = estado;
    }

    public boolean isPreseleccionAbierta() {
        return Boolean.TRUE.equals(preseleccionAbierta);
    }

    public void setPreseleccionAbierta(boolean preseleccionAbierta) {
        this.preseleccionAbierta = preseleccionAbierta;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }
}
