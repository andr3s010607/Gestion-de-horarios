package co.edu.unbosque.gestorhorarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.AulaDTO;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.repository.AulaRepository;

@Service
public class AulaService implements CRUDOperation<AulaDTO> {

    private final AulaRepository aulaRepository;

    public AulaService(AulaRepository aulaRepository) {
        this.aulaRepository = aulaRepository;
    }

    @Override
    public int create(AulaDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            Aula entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            aulaRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<AulaDTO> getAll() {
        return aulaRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public AulaDTO getById(Long id) {
        return aulaRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!aulaRepository.existsById(id)) {
            return 1;
        }
        aulaRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, AulaDTO dto) {
        try {
            if (!aulaRepository.existsById(id) || dto == null) {
                return 1;
            }
            Aula entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            aulaRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return aulaRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return aulaRepository.existsById(id);
    }

    private Aula convertirAEntidad(AulaDTO dto) {
        Aula entity = new Aula();
        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setCapacidad(dto.getCapacidad());
        entity.setUbicacion(dto.getUbicacion());
        entity.setActiva(dto.isActiva());
        return entity;
    }

    private AulaDTO convertirADTO(Aula entity) {
        return new AulaDTO(entity.getId(), entity.getCodigo(), entity.getNombre(), entity.getCapacidad(), entity.getUbicacion(), entity.isActiva());
    }
}

