package co.edu.unbosque.gestorhorarios.dto;

import co.edu.unbosque.gestorhorarios.enums.TipoVinculacion;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocenteDTO {

    private Long id;
    private String nombre;
    private TipoVinculacion vinculacion;
    private List<RestriccionHorariaDocenteDTO> restricciones;
    private List<AsignaturaDTO> asignaturasHabilitadas;

    public DocenteDTO() {
    }

    public DocenteDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public DocenteDTO(Long id, String nombre, TipoVinculacion vinculacion,
            List<RestriccionHorariaDocenteDTO> restricciones, List<AsignaturaDTO> asignaturasHabilitadas) {
        this.id = id;
        this.nombre = nombre;
        this.vinculacion = vinculacion;
        this.restricciones = restricciones;
        this.asignaturasHabilitadas = asignaturasHabilitadas;
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

    public List<RestriccionHorariaDocenteDTO> getRestricciones() {
        return restricciones;
    }

    public void setRestricciones(List<RestriccionHorariaDocenteDTO> restricciones) {
        this.restricciones = restricciones;
    }

    public List<AsignaturaDTO> getAsignaturasHabilitadas() {
        return asignaturasHabilitadas;
    }

    public void setAsignaturasHabilitadas(List<AsignaturaDTO> asignaturasHabilitadas) {
        this.asignaturasHabilitadas = asignaturasHabilitadas;
    }
}
