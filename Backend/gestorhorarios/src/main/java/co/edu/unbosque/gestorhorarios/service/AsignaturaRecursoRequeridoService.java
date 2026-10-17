package co.edu.unbosque.gestorhorarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.AsignaturaRecursoRequeridoDTO;
import co.edu.unbosque.gestorhorarios.entity.AsignaturaRecursoRequerido;
import co.edu.unbosque.gestorhorarios.repository.AsignaturaRecursoRequeridoRepository;
import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.repository.AsignaturaRepository;
import co.edu.unbosque.gestorhorarios.entity.RecursoAula;
import co.edu.unbosque.gestorhorarios.repository.RecursoAulaRepository;

@Service
public class AsignaturaRecursoRequeridoService implements CRUDOperation<AsignaturaRecursoRequeridoDTO> {

    private final AsignaturaRecursoRequeridoRepository asignaturaRecursoRequeridoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final RecursoAulaRepository recursoAulaRepository;

    public AsignaturaRecursoRequeridoService(AsignaturaRecursoRequeridoRepository asignaturaRecursoRequeridoRepository, AsignaturaRepository asignaturaRepository, RecursoAulaRepository recursoAulaRepository) {
        this.asignaturaRecursoRequeridoRepository = asignaturaRecursoRequeridoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.recursoAulaRepository = recursoAulaRepository;
    }

    @Override
    public int create(AsignaturaRecursoRequeridoDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            AsignaturaRecursoRequerido entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            asignaturaRecursoRequeridoRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<AsignaturaRecursoRequeridoDTO> getAll() {
        return asignaturaRecursoRequeridoRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public AsignaturaRecursoRequeridoDTO getById(Long id) {
        return asignaturaRecursoRequeridoRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!asignaturaRecursoRequeridoRepository.existsById(id)) {
            return 1;
        }
        asignaturaRecursoRequeridoRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, AsignaturaRecursoRequeridoDTO dto) {
        try {
            if (!asignaturaRecursoRequeridoRepository.existsById(id) || dto == null) {
                return 1;
            }
            AsignaturaRecursoRequerido entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            asignaturaRecursoRequeridoRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return asignaturaRecursoRequeridoRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return asignaturaRecursoRequeridoRepository.existsById(id);
    }

    private AsignaturaRecursoRequerido convertirAEntidad(AsignaturaRecursoRequeridoDTO dto) {
        AsignaturaRecursoRequerido entity = new AsignaturaRecursoRequerido();
        Asignatura asignatura = dto.getAsignaturaId() == null ? null : asignaturaRepository.findById(dto.getAsignaturaId()).orElse(null);
        if (asignatura == null) {
            return null;
        }
        entity.setAsignatura(asignatura);
        RecursoAula recurso = dto.getRecursoId() == null ? null : recursoAulaRepository.findById(dto.getRecursoId()).orElse(null);
        if (recurso == null) {
            return null;
        }
        entity.setRecurso(recurso);
        return entity;
    }

    private AsignaturaRecursoRequeridoDTO convertirADTO(AsignaturaRecursoRequerido entity) {
        return new AsignaturaRecursoRequeridoDTO(entity.getId(), entity.getAsignatura().getId(), entity.getRecurso().getId());
    }
}

