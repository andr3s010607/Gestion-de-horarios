package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.AulaDTO;
import co.edu.unbosque.gestorhorarios.entity.Aula;
import co.edu.unbosque.gestorhorarios.exception.DatoDuplicadoException;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.exception.OperacionNoPermitidaException;
import co.edu.unbosque.gestorhorarios.exception.RecursoNoEncontradoException;
import co.edu.unbosque.gestorhorarios.repository.AulaRepository;
import co.edu.unbosque.gestorhorarios.repository.SesionClaseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AulaService implements CRUDOperation<AulaDTO> {

    private static final int LONGITUD_MAXIMA_CODIGO = 20;

    private final AulaRepository aulaRepository;
    private final SesionClaseRepository sesionClaseRepository;
    private final ProteccionHorarioAprobadoService proteccionHorarioAprobado;

    public AulaService(AulaRepository aulaRepository, SesionClaseRepository sesionClaseRepository,
            ProteccionHorarioAprobadoService proteccionHorarioAprobado) {
        this.aulaRepository = aulaRepository;
        this.sesionClaseRepository = sesionClaseRepository;
        this.proteccionHorarioAprobado = proteccionHorarioAprobado;
    }

    @Override
    public int create(AulaDTO dto) {
        validarCampos(dto);
        if (aulaRepository.existsByCodigoIgnoreCase(dto.getCodigo().trim())) {
            throw new DatoDuplicadoException("Ya existe un aula con el código " + dto.getCodigo().trim());
        }
        Aula aula = new Aula();
        copiarCampos(dto, aula);
        aulaRepository.save(aula);
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AulaDTO> getAll() {
        return aulaRepository.findAllByOrderByCodigoAsc().stream().map(this::convertirADTO).toList();
    }

    @Transactional(readOnly = true)
    public AulaDTO getById(Long id) {
        return convertirADTO(buscar(id));
    }

    @Override
    public int deleteById(Long id) {
        Aula aula = buscar(id);
        if (sesionClaseRepository.existsByAulaId(id)) {
            throw new OperacionNoPermitidaException("No se puede eliminar el aula porque tiene sesiones asignadas");
        }
        aulaRepository.delete(aula);
        return 0;
    }

    @Override
    public int updateById(Long id, AulaDTO dto) {
        Aula aula = buscar(id);
        validarCampos(dto);
        if (aulaRepository.existsByCodigoIgnoreCaseAndIdNot(dto.getCodigo().trim(), id)) {
            throw new DatoDuplicadoException("Ya existe un aula con el código " + dto.getCodigo().trim());
        }
        proteccionHorarioAprobado.aplicarSinDanarHorariosAprobados(() -> {
            copiarCampos(dto, aula);
            aulaRepository.save(aula);
        });
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return aulaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exist(Long id) {
        return aulaRepository.existsById(id);
    }

    private Aula buscar(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el aula con id " + id));
    }

    private void validarCampos(AulaDTO dto) {
        if (dto == null) {
            throw new DatoInvalidoException("Los datos del aula son obligatorios");
        }
        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new DatoInvalidoException("El código del aula es obligatorio");
        }
        if (dto.getCodigo().trim().length() > LONGITUD_MAXIMA_CODIGO) {
            throw new DatoInvalidoException(
                    "El código del aula admite hasta " + LONGITUD_MAXIMA_CODIGO + " caracteres");
        }
        if (dto.getCapacidad() == null) {
            throw new DatoInvalidoException("La capacidad del aula es obligatoria");
        }
        if (dto.getCapacidad() <= 0) {
            throw new DatoInvalidoException("La capacidad del aula debe ser un entero positivo");
        }
        if (dto.getTipo() == null) {
            throw new DatoInvalidoException("El tipo de aula es obligatorio");
        }
    }

    private void copiarCampos(AulaDTO dto, Aula aula) {
        aula.setCodigo(dto.getCodigo().trim());
        aula.setCapacidad(dto.getCapacidad());
        aula.setTipo(dto.getTipo());
    }

    private AulaDTO convertirADTO(Aula aula) {
        return new AulaDTO(aula.getId(), aula.getCodigo(), aula.getCapacidad(), aula.getTipo());
    }
}
