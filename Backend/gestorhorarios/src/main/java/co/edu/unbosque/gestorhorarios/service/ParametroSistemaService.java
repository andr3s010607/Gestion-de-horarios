package co.edu.unbosque.gestorhorarios.service;

import java.util.List;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import co.edu.unbosque.gestorhorarios.dto.ParametroSistemaDTO;
import co.edu.unbosque.gestorhorarios.entity.ParametroSistema;
import co.edu.unbosque.gestorhorarios.repository.ParametroSistemaRepository;
import co.edu.unbosque.gestorhorarios.enums.TipoDatoParametro;

@Service
public class ParametroSistemaService implements CRUDOperation<ParametroSistemaDTO> {

    private final ParametroSistemaRepository parametroSistemaRepository;

    public ParametroSistemaService(ParametroSistemaRepository parametroSistemaRepository) {
        this.parametroSistemaRepository = parametroSistemaRepository;
    }

    @Override
    public int create(ParametroSistemaDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            if (!validar(dto).isEmpty() || parametroSistemaRepository.findByClave(dto.getClave()).isPresent()) {
                return 1;
            }
            ParametroSistema entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            parametroSistemaRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<ParametroSistemaDTO> getAll() {
        return parametroSistemaRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public ParametroSistemaDTO getById(Long id) {
        return parametroSistemaRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!parametroSistemaRepository.existsById(id)) {
            return 1;
        }
        parametroSistemaRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, ParametroSistemaDTO dto) {
        try {
            if (!parametroSistemaRepository.existsById(id) || dto == null) {
                return 1;
            }
            if (!validar(dto).isEmpty()
                    || parametroSistemaRepository.findByClave(dto.getClave())
                            .filter(parametro -> !parametro.getId().equals(id)).isPresent()) {
                return 1;
            }
            ParametroSistema entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            entity.setId(id);
            parametroSistemaRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return parametroSistemaRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return parametroSistemaRepository.existsById(id);
    }

    public List<String> validar(ParametroSistemaDTO dto) {
        if (dto == null || dto.getClave() == null || dto.getClave().isBlank() || dto.getValor() == null
                || dto.getTipoDato() == null) {
            return List.of("La clave, el valor y el tipo de dato son obligatorios");
        }
        try {
            switch (dto.getTipoDato()) {
                case ENTERO -> Integer.parseInt(dto.getValor());
                case DECIMAL -> new java.math.BigDecimal(dto.getValor());
                case BOOLEANO -> {
                    if (!"true".equalsIgnoreCase(dto.getValor()) && !"false".equalsIgnoreCase(dto.getValor())) {
                        throw new IllegalArgumentException();
                    }
                }
                case HORA -> LocalTime.parse(dto.getValor());
                case TEXTO -> {
                }
            }
        } catch (Exception e) {
            return List.of("El valor no corresponde al tipo de dato indicado");
        }
        return List.of();
    }

    private ParametroSistema convertirAEntidad(ParametroSistemaDTO dto) {
        ParametroSistema entity = new ParametroSistema();
        entity.setClave(dto.getClave());
        entity.setValor(dto.getValor());
        entity.setTipoDato(dto.getTipoDato());
        entity.setDescripcion(dto.getDescripcion());
        entity.setActivo(dto.isActivo());
        return entity;
    }

    private ParametroSistemaDTO convertirADTO(ParametroSistema entity) {
        return new ParametroSistemaDTO(entity.getId(), entity.getClave(), entity.getValor(), entity.getTipoDato(), entity.getDescripcion(), entity.isActivo());
    }
}

