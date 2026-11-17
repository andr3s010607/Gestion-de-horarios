package co.edu.unbosque.gestorhorarios.dto;

public class DocenteAsignaturaDTO {

    private Long docenteId;
    private Long asignaturaId;

    public DocenteAsignaturaDTO() {
    }

    public DocenteAsignaturaDTO(Long docenteId, Long asignaturaId) {
        this.docenteId = docenteId;
        this.asignaturaId = asignaturaId;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }
}
