package co.edu.unbosque.gestorhorarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.RecursoAulaDTO;
import co.edu.unbosque.gestorhorarios.entity.RecursoAula;
import co.edu.unbosque.gestorhorarios.repository.RecursoAulaRepository;

@Service
public class RecursoAulaService implements CRUDOperation<RecursoAulaDTO> {

    private final RecursoAulaRepository recursoAulaRepository;

    public RecursoAulaService(RecursoAulaRepository recursoAulaRepository) {
        this.recursoAulaRepository = recursoAulaRepository;
    }

    @Override
    public int create(RecursoAulaDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            RecursoAula entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            recursoAulaRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<RecursoAulaDTO> getAll() {
        return recursoAulaRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public RecursoAulaDTO getById(Long id) {
        return recursoAulaRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!recursoAulaRepository.existsById(id)) {
            return 1;
        }
        recursoAulaRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, RecursoAulaDTO dto) {
        try {
            if (!recursoAulaRepository.existsById(id) || dto == null) {
                return 1;
            }
            RecursoAula entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            recursoAulaRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return recursoAulaRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return recursoAulaRepository.existsById(id);
    }

    private RecursoAula convertirAEntidad(RecursoAulaDTO dto) {
        RecursoAula entity = new RecursoAula();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }

    private RecursoAulaDTO convertirADTO(RecursoAula entity) {
        return new RecursoAulaDTO(entity.getId(), entity.getNombre(), entity.getDescripcion());
    }
}

