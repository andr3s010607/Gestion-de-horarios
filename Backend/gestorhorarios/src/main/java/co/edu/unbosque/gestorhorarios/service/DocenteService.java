package co.edu.unbosque.gestorhorarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.DocenteDTO;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.repository.DocenteRepository;
import co.edu.unbosque.gestorhorarios.enums.TipoVinculacion;

@Service
public class DocenteService implements CRUDOperation<DocenteDTO> {

    private final DocenteRepository docenteRepository;

    public DocenteService(DocenteRepository docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    @Override
    public int create(DocenteDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            Docente entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            docenteRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<DocenteDTO> getAll() {
        return docenteRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public DocenteDTO getById(Long id) {
        return docenteRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!docenteRepository.existsById(id)) {
            return 1;
        }
        docenteRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, DocenteDTO dto) {
        try {
            if (!docenteRepository.existsById(id) || dto == null) {
                return 1;
            }
            Docente entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            docenteRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return docenteRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return docenteRepository.existsById(id);
    }

    private Docente convertirAEntidad(DocenteDTO dto) {
        Docente entity = new Docente();
        entity.setNombres(dto.getNombres());
        entity.setApellidos(dto.getApellidos());
        entity.setCorreo(dto.getCorreo());
        entity.setTipoVinculacion(dto.getTipoVinculacion());
        entity.setActivo(dto.isActivo());
        return entity;
    }

    private DocenteDTO convertirADTO(Docente entity) {
        return new DocenteDTO(entity.getId(), entity.getNombres(), entity.getApellidos(), entity.getCorreo(), entity.getTipoVinculacion(), entity.isActivo());
    }
}

