package co.edu.unbosque.gestorhorarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.DocenteAsignaturaDTO;
import co.edu.unbosque.gestorhorarios.entity.DocenteAsignatura;
import co.edu.unbosque.gestorhorarios.repository.DocenteAsignaturaRepository;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.repository.DocenteRepository;
import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.repository.AsignaturaRepository;

@Service
public class DocenteAsignaturaService implements CRUDOperation<DocenteAsignaturaDTO> {

    private final DocenteAsignaturaRepository docenteAsignaturaRepository;
    private final DocenteRepository docenteRepository;
    private final AsignaturaRepository asignaturaRepository;

    public DocenteAsignaturaService(DocenteAsignaturaRepository docenteAsignaturaRepository, DocenteRepository docenteRepository, AsignaturaRepository asignaturaRepository) {
        this.docenteAsignaturaRepository = docenteAsignaturaRepository;
        this.docenteRepository = docenteRepository;
        this.asignaturaRepository = asignaturaRepository;
    }

    @Override
    public int create(DocenteAsignaturaDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            DocenteAsignatura entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            docenteAsignaturaRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<DocenteAsignaturaDTO> getAll() {
        return docenteAsignaturaRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public DocenteAsignaturaDTO getById(Long id) {
        return docenteAsignaturaRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!docenteAsignaturaRepository.existsById(id)) {
            return 1;
        }
        docenteAsignaturaRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, DocenteAsignaturaDTO dto) {
        try {
            if (!docenteAsignaturaRepository.existsById(id) || dto == null) {
                return 1;
            }
            DocenteAsignatura entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            docenteAsignaturaRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return docenteAsignaturaRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return docenteAsignaturaRepository.existsById(id);
    }

    private DocenteAsignatura convertirAEntidad(DocenteAsignaturaDTO dto) {
        DocenteAsignatura entity = new DocenteAsignatura();
        Docente docente = dto.getDocenteId() == null ? null : docenteRepository.findById(dto.getDocenteId()).orElse(null);
        if (docente == null) {
            return null;
        }
        entity.setDocente(docente);
        Asignatura asignatura = dto.getAsignaturaId() == null ? null : asignaturaRepository.findById(dto.getAsignaturaId()).orElse(null);
        if (asignatura == null) {
            return null;
        }
        entity.setAsignatura(asignatura);
        return entity;
    }

    private DocenteAsignaturaDTO convertirADTO(DocenteAsignatura entity) {
        return new DocenteAsignaturaDTO(entity.getId(), entity.getDocente().getId(), entity.getAsignatura().getId());
    }
}

