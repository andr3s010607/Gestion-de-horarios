package co.edu.unbosque.gestorhorarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.GrupoDTO;
import co.edu.unbosque.gestorhorarios.entity.Grupo;
import co.edu.unbosque.gestorhorarios.repository.GrupoRepository;
import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.repository.AsignaturaRepository;
import co.edu.unbosque.gestorhorarios.entity.Semestre;
import co.edu.unbosque.gestorhorarios.repository.SemestreRepository;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.repository.DocenteRepository;

@Service
public class GrupoService implements CRUDOperation<GrupoDTO> {

    private final GrupoRepository grupoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final SemestreRepository semestreRepository;
    private final DocenteRepository docenteRepository;

    public GrupoService(GrupoRepository grupoRepository, AsignaturaRepository asignaturaRepository, SemestreRepository semestreRepository, DocenteRepository docenteRepository) {
        this.grupoRepository = grupoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.semestreRepository = semestreRepository;
        this.docenteRepository = docenteRepository;
    }

    @Override
    public int create(GrupoDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            Grupo entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            grupoRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<GrupoDTO> getAll() {
        return grupoRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public GrupoDTO getById(Long id) {
        return grupoRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!grupoRepository.existsById(id)) {
            return 1;
        }
        grupoRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, GrupoDTO dto) {
        try {
            if (!grupoRepository.existsById(id) || dto == null) {
                return 1;
            }
            Grupo entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            grupoRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return grupoRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return grupoRepository.existsById(id);
    }

    private Grupo convertirAEntidad(GrupoDTO dto) {
        Grupo entity = new Grupo();
        entity.setCodigo(dto.getCodigo());
        entity.setCupoPlaneado(dto.getCupoPlaneado());
        entity.setActivo(dto.isActivo());
        Asignatura asignatura = dto.getAsignaturaId() == null ? null : asignaturaRepository.findById(dto.getAsignaturaId()).orElse(null);
        if (asignatura == null) {
            return null;
        }
        entity.setAsignatura(asignatura);
        Semestre semestre = dto.getSemestreId() == null ? null : semestreRepository.findById(dto.getSemestreId()).orElse(null);
        if (semestre == null) {
            return null;
        }
        entity.setSemestre(semestre);
        Docente docente = dto.getDocenteId() == null ? null : docenteRepository.findById(dto.getDocenteId()).orElse(null);
        if (dto.getDocenteId() != null && docente == null) {
            return null;
        }
        entity.setDocente(docente);
        return entity;
    }

    private GrupoDTO convertirADTO(Grupo entity) {
        return new GrupoDTO(entity.getId(), entity.getCodigo(), entity.getCupoPlaneado(), entity.isActivo(), entity.getAsignatura().getId(), entity.getSemestre().getId(), entity.getDocente() == null ? null : entity.getDocente().getId());
    }
}

