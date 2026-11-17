package co.edu.unbosque.gestorhorarios.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Grupos elegidos por un estudiante en un horario. Se usa para revisar los
 * choques de un borrador (RF-33), confirmarlo (RF-34) y consultar la
 * preselección confirmada.
 */
public class PreseleccionDTO {

    private Long horarioId;
    private List<Long> grupoIds = new ArrayList<>();

    public PreseleccionDTO() {
    }

    public PreseleccionDTO(Long horarioId, List<Long> grupoIds) {
        this.horarioId = horarioId;
        this.grupoIds = grupoIds;
    }

    public Long getHorarioId() {
        return horarioId;
    }

    public void setHorarioId(Long horarioId) {
        this.horarioId = horarioId;
    }

    public List<Long> getGrupoIds() {
        return grupoIds;
    }

    public void setGrupoIds(List<Long> grupoIds) {
        this.grupoIds = grupoIds;
    }
}
