package co.edu.unbosque.gestorhorarios.exception;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import java.util.List;

public class IncidenciasHorarioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final transient List<IncidenciaDTO> incidencias;
    private final int sesionesSinUbicar;

    public IncidenciasHorarioException(String mensaje, List<IncidenciaDTO> incidencias) {
        this(mensaje, incidencias, 0);
    }

    public IncidenciasHorarioException(String mensaje, List<IncidenciaDTO> incidencias, int sesionesSinUbicar) {
        super(mensaje);
        this.incidencias = incidencias;
        this.sesionesSinUbicar = sesionesSinUbicar;
    }

    public List<IncidenciaDTO> getIncidencias() {
        return incidencias;
    }

    public int getSesionesSinUbicar() {
        return sesionesSinUbicar;
    }
}
