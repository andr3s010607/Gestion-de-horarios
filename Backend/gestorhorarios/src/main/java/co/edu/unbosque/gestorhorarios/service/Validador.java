package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.entity.Horario;
import java.util.List;

public interface Validador {

    List<IncidenciaDTO> validar(Horario horario);
}
