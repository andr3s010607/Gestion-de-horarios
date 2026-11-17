package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.RestriccionHorariaDocenteDTO;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.entity.RestriccionHorariaDocente;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.exception.RecursoNoEncontradoException;
import co.edu.unbosque.gestorhorarios.repository.DocenteRepository;
import co.edu.unbosque.gestorhorarios.repository.RestriccionHorariaDocenteRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RestriccionHorariaDocenteService implements CRUDOperation<RestriccionHorariaDocenteDTO> {

    private final RestriccionHorariaDocenteRepository restriccionRepository;
    private final DocenteRepository docenteRepository;
    private final ProteccionHorarioAprobadoService proteccionHorarioAprobado;

    public RestriccionHorariaDocenteService(RestriccionHorariaDocenteRepository restriccionRepository,
            DocenteRepository docenteRepository, ProteccionHorarioAprobadoService proteccionHorarioAprobado) {
        this.restriccionRepository = restriccionRepository;
        this.docenteRepository = docenteRepository;
        this.proteccionHorarioAprobado = proteccionHorarioAprobado;
    }

    @Override
    public int create(RestriccionHorariaDocenteDTO dto) {
        validarCampos(dto);
        RestriccionHorariaDocente restriccion = new RestriccionHorariaDocente();
        proteccionHorarioAprobado.aplicarSinDanarHorariosAprobados(() -> {
            copiarCampos(dto, restriccion);
            restriccionRepository.save(restriccion);
        });
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestriccionHorariaDocenteDTO> getAll() {
        return ordenarYConvertir(restriccionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<RestriccionHorariaDocenteDTO> getByDocenteId(Long docenteId) {
        return ordenarYConvertir(restriccionRepository.findByDocenteId(docenteId));
    }

    @Transactional(readOnly = true)
    public RestriccionHorariaDocenteDTO getById(Long id) {
        return convertirADTO(buscar(id));
    }

    @Override
    public int deleteById(Long id) {
        restriccionRepository.delete(buscar(id));
        return 0;
    }

    @Override
    public int updateById(Long id, RestriccionHorariaDocenteDTO dto) {
        RestriccionHorariaDocente restriccion = buscar(id);
        validarCampos(dto);
        proteccionHorarioAprobado.aplicarSinDanarHorariosAprobados(() -> {
            copiarCampos(dto, restriccion);
            restriccionRepository.save(restriccion);
        });
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return restriccionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exist(Long id) {
        return restriccionRepository.existsById(id);
    }

    private RestriccionHorariaDocente buscar(Long id) {
        return restriccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la restricción de horario con id " + id));
    }

    private void validarCampos(RestriccionHorariaDocenteDTO dto) {
        if (dto == null) {
            throw new DatoInvalidoException("Los datos de la restricción de horario son obligatorios");
        }
        if (dto.getDocenteId() == null) {
            throw new DatoInvalidoException("El docente de la restricción es obligatorio");
        }
        if (dto.getDia() == null) {
            throw new DatoInvalidoException("El día de la restricción es obligatorio");
        }
        if (dto.getHoraInicio() == null || dto.getHoraFin() == null) {
            throw new DatoInvalidoException("La hora de inicio y la hora de fin de la restricción son obligatorias");
        }
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            throw new DatoInvalidoException("La hora de fin de la restricción debe ser posterior a la de inicio");
        }
    }

    private void copiarCampos(RestriccionHorariaDocenteDTO dto, RestriccionHorariaDocente restriccion) {
        Docente docente = docenteRepository.findById(dto.getDocenteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el docente con id " + dto.getDocenteId()));
        restriccion.setDocente(docente);
        restriccion.setDia(dto.getDia().getNumero());
        restriccion.setMinutoInicio(ReglaHorarioService.convertirAMinutos(dto.getHoraInicio()));
        restriccion.setMinutoFin(ReglaHorarioService.convertirAMinutos(dto.getHoraFin()));
    }

    private List<RestriccionHorariaDocenteDTO> ordenarYConvertir(List<RestriccionHorariaDocente> restricciones) {
        return restricciones.stream()
                .sorted(Comparator.comparing(RestriccionHorariaDocente::getDia)
                        .thenComparing(RestriccionHorariaDocente::getMinutoInicio))
                .map(this::convertirADTO)
                .toList();
    }

    private RestriccionHorariaDocenteDTO convertirADTO(RestriccionHorariaDocente restriccion) {
        return new RestriccionHorariaDocenteDTO(restriccion.getId(), restriccion.getDocente().getId(),
                DiaSemana.desdeNumero(restriccion.getDia()),
                ReglaHorarioService.convertirAHora(restriccion.getMinutoInicio()),
                ReglaHorarioService.convertirAHora(restriccion.getMinutoFin()));
    }
}
