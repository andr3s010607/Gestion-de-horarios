package co.edu.unbosque.gestorhorarios.dto;

import co.edu.unbosque.gestorhorarios.enums.EstadoHorario;

public class HorarioDTO {

    private Long id;
    private String semestre;
    private EstadoHorario estado;
    private boolean preseleccionAbierta;

    public HorarioDTO() {
    }

    public HorarioDTO(Long id, String semestre, EstadoHorario estado) {
        this.id = id;
        this.semestre = semestre;
        this.estado = estado;
    }

    public HorarioDTO(Long id, String semestre, EstadoHorario estado, boolean preseleccionAbierta) {
        this(id, semestre, estado);
        this.preseleccionAbierta = preseleccionAbierta;
    }

    public boolean isPreseleccionAbierta() {
        return preseleccionAbierta;
    }

    public void setPreseleccionAbierta(boolean preseleccionAbierta) {
        this.preseleccionAbierta = preseleccionAbierta;
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
}
