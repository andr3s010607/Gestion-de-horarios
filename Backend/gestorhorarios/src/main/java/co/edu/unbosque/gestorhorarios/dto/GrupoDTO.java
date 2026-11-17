package co.edu.unbosque.gestorhorarios.dto;

public class GrupoDTO {

    private Long id;
    private Integer numero;
    private Integer cupo;
    private Long asignaturaId;
    private Long horarioId;
    private Long docenteId;

    public GrupoDTO() {
    }

    public GrupoDTO(Long id, Integer numero, Integer cupo, Long asignaturaId, Long horarioId, Long docenteId) {
        this.id = id;
        this.numero = numero;
        this.cupo = cupo;
        this.asignaturaId = asignaturaId;
        this.horarioId = horarioId;
        this.docenteId = docenteId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getCupo() {
        return cupo;
    }

    public void setCupo(Integer cupo) {
        this.cupo = cupo;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public Long getHorarioId() {
        return horarioId;
    }

    public void setHorarioId(Long horarioId) {
        this.horarioId = horarioId;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }
}
