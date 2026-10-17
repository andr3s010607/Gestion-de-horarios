package co.edu.unbosque.gestorhorarios.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.RestriccionHorariaDocenteDTO;
import co.edu.unbosque.gestorhorarios.entity.RestriccionHorariaDocente;
import co.edu.unbosque.gestorhorarios.repository.RestriccionHorariaDocenteRepository;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.repository.DocenteRepository;
import co.edu.unbosque.gestorhorarios.enums.DiaSemana;
import co.edu.unbosque.gestorhorarios.enums.TipoRestriccionHorario;

@Service
public class RestriccionHorariaDocenteService implements CRUDOperation<RestriccionHorariaDocenteDTO> {

    private final RestriccionHorariaDocenteRepository restriccionHorariaDocenteRepository;
    private final DocenteRepository docenteRepository;

    public RestriccionHorariaDocenteService(RestriccionHorariaDocenteRepository restriccionHorariaDocenteRepository, DocenteRepository docenteRepository) {
        this.restriccionHorariaDocenteRepository = restriccionHorariaDocenteRepository;
        this.docenteRepository = docenteRepository;
    }

    @Override
    public int create(RestriccionHorariaDocenteDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            RestriccionHorariaDocente entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            restriccionHorariaDocenteRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<RestriccionHorariaDocenteDTO> getAll() {
        return restriccionHorariaDocenteRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public RestriccionHorariaDocenteDTO getById(Long id) {
        return restriccionHorariaDocenteRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!restriccionHorariaDocenteRepository.existsById(id)) {
            return 1;
        }
        restriccionHorariaDocenteRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, RestriccionHorariaDocenteDTO dto) {
        try {
            if (!restriccionHorariaDocenteRepository.existsById(id) || dto == null) {
                return 1;
            }
            RestriccionHorariaDocente entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            restriccionHorariaDocenteRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return restriccionHorariaDocenteRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return restriccionHorariaDocenteRepository.existsById(id);
    }

    private RestriccionHorariaDocente convertirAEntidad(RestriccionHorariaDocenteDTO dto) {
        RestriccionHorariaDocente entity = new RestriccionHorariaDocente();
        entity.setDia(dto.getDia());
        entity.setHoraInicio(dto.getHoraInicio());
        entity.setHoraFin(dto.getHoraFin());
        entity.setTipo(dto.getTipo());
        Docente docente = dto.getDocenteId() == null ? null : docenteRepository.findById(dto.getDocenteId()).orElse(null);
        if (docente == null) {
            return null;
        }
        entity.setDocente(docente);
        return entity;
    }

    private RestriccionHorariaDocenteDTO convertirADTO(RestriccionHorariaDocente entity) {
        return new RestriccionHorariaDocenteDTO(entity.getId(), entity.getDocente().getId(), entity.getDia(), entity.getHoraInicio(), entity.getHoraFin(), entity.getTipo());
    }
}

