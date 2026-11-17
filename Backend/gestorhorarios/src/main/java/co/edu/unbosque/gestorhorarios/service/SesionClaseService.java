package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.SesionClaseDTO;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.exception.RecursoNoEncontradoException;
import co.edu.unbosque.gestorhorarios.repository.SesionClaseRepository;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SesionClaseService {

    private final SesionClaseRepository sesionClaseRepository;
    private final ReglaHorarioService reglaHorarioService;

    public SesionClaseService(SesionClaseRepository sesionClaseRepository, ReglaHorarioService reglaHorarioService) {
        this.sesionClaseRepository = sesionClaseRepository;
        this.reglaHorarioService = reglaHorarioService;
    }

    @Transactional(readOnly = true)
    public List<SesionClaseDTO> getAll() {
        return ordenarYConvertir(sesionClaseRepository.findAll());
    }

    @Transactional(readOnly = true)
    public SesionClaseDTO getById(Long id) {
        return convertirADTO(buscar(id));
    }

    public SesionClase buscar(Long id) {
        return sesionClaseRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la sesión con id " + id));
    }

    /**
     * Envía a la base de datos los cambios de ubicación pendientes. Las filas de
     * ocupacion_aula y ocupacion_docente las reconstruye el disparador
     * trg_sesion_ocupacion; si la nueva ubicación choca con otra sesión, la
     * restricción UNIQUE de esas tablas hace fallar esta operación.
     */
    public void guardarUbicaciones() {
        sesionClaseRepository.flush();
    }

    public List<SesionClaseDTO> ordenarYConvertir(Collection<SesionClase> sesiones) {
        return sesiones.stream()
                .sorted(Comparator
                        .comparing(SesionClase::getDia, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(SesionClase::getMinutoInicio, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(sesion -> sesion.getGrupo().getAsignatura().getCodigo())
                        .thenComparing(sesion -> sesion.getGrupo().getNumero())
                        .thenComparing(SesionClase::getId))
                .map(this::convertirADTO)
                .toList();
    }

    public SesionClaseDTO convertirADTO(SesionClase sesion) {
        Grupo grupo = sesion.getGrupo();
        SesionClaseDTO dto = new SesionClaseDTO();
        dto.setId(sesion.getId());
        dto.setHorarioId(sesion.getHorario().getId());
        dto.setGrupoId(grupo.getId());
        dto.setNumeroGrupo(grupo.getNumero());
        dto.setAsignaturaId(grupo.getAsignatura().getId());
        dto.setCodigoAsignatura(grupo.getAsignatura().getCodigo());
        dto.setNombreAsignatura(grupo.getAsignatura().getNombre());
        if (grupo.getDocente() != null) {
            dto.setDocenteId(grupo.getDocente().getId());
            dto.setNombreDocente(grupo.getDocente().getNombre());
        }
        if (sesion.getAula() != null) {
            dto.setAulaId(sesion.getAula().getId());
            dto.setCodigoAula(sesion.getAula().getCodigo());
        }
        dto.setUbicada(sesion.estaUbicada());
        if (sesion.estaUbicada()) {
            dto.setDia(DiaSemana.desdeNumero(sesion.getDia()));
            dto.setHoraInicio(ReglaHorarioService.convertirAHora(sesion.getMinutoInicio()));
            dto.setHoraFin(ReglaHorarioService.convertirAHora(sesion.getMinutoFin()));
            dto.setDuracionHoras((sesion.getMinutoFin() - sesion.getMinutoInicio())
                    / ReglaHorarioService.MINUTOS_POR_HORA);
        } else {
            dto.setDuracionHoras(reglaHorarioService.duracionSesionHoras());
        }
        return dto;
    }
}
