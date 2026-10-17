package co.edu.unbosque.gestorhorarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.AulaRecursoDTO;
import co.edu.unbosque.gestorhorarios.entity.AulaRecurso;
import co.edu.unbosque.gestorhorarios.repository.AulaRecursoRepository;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.repository.AulaRepository;
import co.edu.unbosque.gestorhorarios.entity.RecursoAula;
import co.edu.unbosque.gestorhorarios.repository.RecursoAulaRepository;

@Service
public class AulaRecursoService implements CRUDOperation<AulaRecursoDTO> {

    private final AulaRecursoRepository aulaRecursoRepository;
    private final AulaRepository aulaRepository;
    private final RecursoAulaRepository recursoAulaRepository;

    public AulaRecursoService(AulaRecursoRepository aulaRecursoRepository, AulaRepository aulaRepository, RecursoAulaRepository recursoAulaRepository) {
        this.aulaRecursoRepository = aulaRecursoRepository;
        this.aulaRepository = aulaRepository;
        this.recursoAulaRepository = recursoAulaRepository;
    }

    @Override
    public int create(AulaRecursoDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            AulaRecurso entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            aulaRecursoRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<AulaRecursoDTO> getAll() {
        return aulaRecursoRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public AulaRecursoDTO getById(Long id) {
        return aulaRecursoRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!aulaRecursoRepository.existsById(id)) {
            return 1;
        }
        aulaRecursoRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, AulaRecursoDTO dto) {
        try {
            if (!aulaRecursoRepository.existsById(id) || dto == null) {
                return 1;
            }
            AulaRecurso entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            aulaRecursoRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return aulaRecursoRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return aulaRecursoRepository.existsById(id);
    }

    private AulaRecurso convertirAEntidad(AulaRecursoDTO dto) {
        AulaRecurso entity = new AulaRecurso();
        Aula aula = dto.getAulaId() == null ? null : aulaRepository.findById(dto.getAulaId()).orElse(null);
        if (aula == null) {
            return null;
        }
        entity.setAula(aula);
        RecursoAula recurso = dto.getRecursoId() == null ? null : recursoAulaRepository.findById(dto.getRecursoId()).orElse(null);
        if (recurso == null) {
            return null;
        }
        entity.setRecurso(recurso);
        return entity;
    }

    private AulaRecursoDTO convertirADTO(AulaRecurso entity) {
        return new AulaRecursoDTO(entity.getId(), entity.getAula().getId(), entity.getRecurso().getId());
    }
}

