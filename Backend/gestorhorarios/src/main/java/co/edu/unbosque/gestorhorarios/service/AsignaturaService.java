package co.edu.unbosque.gestorhorarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.AsignaturaDTO;
import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.repository.AsignaturaRepository;

@Service
public class AsignaturaService implements CRUDOperation<AsignaturaDTO> {

    private final AsignaturaRepository asignaturaRepository;

    public AsignaturaService(AsignaturaRepository asignaturaRepository) {
        this.asignaturaRepository = asignaturaRepository;
    }

    @Override
    public int create(AsignaturaDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            Asignatura entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            asignaturaRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<AsignaturaDTO> getAll() {
        return asignaturaRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public AsignaturaDTO getById(Long id) {
        return asignaturaRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    public AsignaturaDTO getByCodigo(String codigo) {
        return asignaturaRepository.findByCodigo(codigo).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!asignaturaRepository.existsById(id)) {
            return 1;
        }
        asignaturaRepository.deleteById(id);
        return 0;
    }

    public int deleteByCodigo(String codigo) {
        if (asignaturaRepository.findByCodigo(codigo).isEmpty()) {
            return 1;
        }
        asignaturaRepository.deleteByCodigo(codigo);
        return 0;
    }

    @Override
    public int updateById(Long id, AsignaturaDTO dto) {
        try {
            if (!asignaturaRepository.existsById(id) || dto == null) {
                return 1;
            }
            Asignatura entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            asignaturaRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    public int updateByCodigo(String codigo, AsignaturaDTO dto) {
        Asignatura asignatura = asignaturaRepository.findByCodigo(codigo).orElse(null);
        return asignatura == null ? 1 : updateById(asignatura.getId(), dto);
    }

    @Override
    public long count() {
        return asignaturaRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return asignaturaRepository.existsById(id);
    }

    private Asignatura convertirAEntidad(AsignaturaDTO dto) {
        Asignatura entity = new Asignatura();
        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setSesionesSemanales(dto.getSesionesSemanales());
        entity.setActiva(dto.isActiva());
        return entity;
    }

    private AsignaturaDTO convertirADTO(Asignatura entity) {
        return new AsignaturaDTO(entity.getId(), entity.getCodigo(), entity.getNombre(), entity.getSesionesSemanales(), entity.isActiva());
    }
}

