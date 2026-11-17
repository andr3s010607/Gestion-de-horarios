package co.edu.unbosque.gestorhorarios.dto;

import java.util.List;

/** Asignatura de un horario con los grupos que el estudiante puede elegir (RF-32). */
public class AsignaturaPreseleccionDTO {

    private Long asignaturaId;
    private String codigo;
    private String nombre;
    private List<GrupoPreseleccionDTO> grupos;

    public AsignaturaPreseleccionDTO() {
    }

    public AsignaturaPreseleccionDTO(Long asignaturaId, String codigo, String nombre,
            List<GrupoPreseleccionDTO> grupos) {
        this.asignaturaId = asignaturaId;
        this.codigo = codigo;
        this.nombre = nombre;
        this.grupos = grupos;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
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

    public List<GrupoPreseleccionDTO> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoPreseleccionDTO> grupos) {
        this.grupos = grupos;
    }
}
