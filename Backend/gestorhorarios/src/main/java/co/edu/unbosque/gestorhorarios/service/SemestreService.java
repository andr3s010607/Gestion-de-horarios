package co.edu.unbosque.gestorhorarios.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.SemestreDTO;
import co.edu.unbosque.gestorhorarios.entity.Semestre;
import co.edu.unbosque.gestorhorarios.repository.SemestreRepository;

@Service
public class SemestreService implements CRUDOperation<SemestreDTO> {

    private final SemestreRepository semestreRepository;

    public SemestreService(SemestreRepository semestreRepository) {
        this.semestreRepository = semestreRepository;
    }

    @Override
    public int create(SemestreDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            Semestre entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            semestreRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<SemestreDTO> getAll() {
        return semestreRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public SemestreDTO getById(Long id) {
        return semestreRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!semestreRepository.existsById(id)) {
            return 1;
        }
        semestreRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, SemestreDTO dto) {
        try {
            if (!semestreRepository.existsById(id) || dto == null) {
                return 1;
            }
            Semestre entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            semestreRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return semestreRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return semestreRepository.existsById(id);
    }

    private Semestre convertirAEntidad(SemestreDTO dto) {
        Semestre entity = new Semestre();
        entity.setAnio(dto.getAnio());
        entity.setPeriodo(dto.getPeriodo());
        entity.setActivo(dto.isActivo());
        return entity;
    }

    private SemestreDTO convertirADTO(Semestre entity) {
        return new SemestreDTO(entity.getId(), entity.getAnio(), entity.getPeriodo(), entity.isActivo());
    }
}

