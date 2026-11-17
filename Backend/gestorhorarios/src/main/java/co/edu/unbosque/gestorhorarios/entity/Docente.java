package co.edu.unbosque.gestorhorarios.entity;

import co.edu.unbosque.gestorhorarios.enums.TipoVinculacion;
import jakarta.persistence.Column;
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
@Table(name = "docente")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVinculacion vinculacion;

    @OneToMany(mappedBy = "docente")
    private List<RestriccionHorariaDocente> restricciones = new ArrayList<>();

    @OneToMany(mappedBy = "docente")
    private List<DocenteAsignatura> habilitaciones = new ArrayList<>();

    public Docente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoVinculacion getVinculacion() {
        return vinculacion;
    }

    public void setVinculacion(TipoVinculacion vinculacion) {
        this.vinculacion = vinculacion;
    }

    public List<RestriccionHorariaDocente> getRestricciones() {
        return restricciones;
    }

    public void setRestricciones(List<RestriccionHorariaDocente> restricciones) {
        this.restricciones = restricciones;
    }

    public List<DocenteAsignatura> getHabilitaciones() {
        return habilitaciones;
    }

    public void setHabilitaciones(List<DocenteAsignatura> habilitaciones) {
        this.habilitaciones = habilitaciones;
    }
}
