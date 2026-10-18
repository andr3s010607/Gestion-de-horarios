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
import co.edu.unbosque.gestorhorarios.repository.GrupoRepository;

@Service
public class DocenteAsignaturaService implements CRUDOperation<DocenteAsignaturaDTO> {

    private final DocenteAsignaturaRepository docenteAsignaturaRepository;
    private final DocenteRepository docenteRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final GrupoRepository grupoRepository;

    public DocenteAsignaturaService(DocenteAsignaturaRepository docenteAsignaturaRepository, DocenteRepository docenteRepository, AsignaturaRepository asignaturaRepository, GrupoRepository grupoRepository) {
        this.docenteAsignaturaRepository = docenteAsignaturaRepository;
        this.docenteRepository = docenteRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.grupoRepository = grupoRepository;
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
        DocenteAsignatura relacion = docenteAsignaturaRepository.findById(id).orElse(null);
        if (relacion == null) {
            return 1;
        }
        boolean enUso = grupoRepository.findAll().stream()
                .anyMatch(grupo -> grupo.getDocente() != null
                        && grupo.getDocente().getId().equals(relacion.getDocente().getId())
                        && grupo.getAsignatura().getId().equals(relacion.getAsignatura().getId()));
        if (enUso) {
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
        if (docente == null || !docente.isActivo()) {
            return null;
        }
        entity.setDocente(docente);
        Asignatura asignatura = dto.getAsignaturaId() == null ? null : asignaturaRepository.findById(dto.getAsignaturaId()).orElse(null);
        if (asignatura == null || !asignatura.isActiva()) {
            return null;
        }
        entity.setAsignatura(asignatura);
        return entity;
    }

    private DocenteAsignaturaDTO convertirADTO(DocenteAsignatura entity) {
        return new DocenteAsignaturaDTO(entity.getId(), entity.getDocente().getId(), entity.getAsignatura().getId());
    }
}

