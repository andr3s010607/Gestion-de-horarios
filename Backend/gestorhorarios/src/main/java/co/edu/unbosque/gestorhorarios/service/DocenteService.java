package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.AsignaturaDTO;
import co.edu.unbosque.gestorhorarios.dto.DocenteDTO;
import co.edu.unbosque.gestorhorarios.dto.RestriccionHorariaDocenteDTO;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.entity.Usuario;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.exception.OperacionNoPermitidaException;
import co.edu.unbosque.gestorhorarios.exception.RecursoNoEncontradoException;
import co.edu.unbosque.gestorhorarios.repository.DocenteAsignaturaRepository;
import co.edu.unbosque.gestorhorarios.repository.DocenteRepository;
import co.edu.unbosque.gestorhorarios.repository.GrupoRepository;
import co.edu.unbosque.gestorhorarios.repository.RestriccionHorariaDocenteRepository;
import co.edu.unbosque.gestorhorarios.repository.UsuarioRepository;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DocenteService implements CRUDOperation<DocenteDTO> {

    private static final int LONGITUD_MAXIMA_NOMBRE = 120;

    private final DocenteRepository docenteRepository;
    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestriccionHorariaDocenteRepository restriccionRepository;
    private final DocenteAsignaturaRepository docenteAsignaturaRepository;
    private final RestriccionHorariaDocenteService restriccionService;
    private final DocenteAsignaturaService docenteAsignaturaService;

    public DocenteService(DocenteRepository docenteRepository, GrupoRepository grupoRepository,
            UsuarioRepository usuarioRepository, RestriccionHorariaDocenteRepository restriccionRepository,
            DocenteAsignaturaRepository docenteAsignaturaRepository,
            RestriccionHorariaDocenteService restriccionService, DocenteAsignaturaService docenteAsignaturaService) {
        this.docenteRepository = docenteRepository;
        this.grupoRepository = grupoRepository;
        this.usuarioRepository = usuarioRepository;
        this.restriccionRepository = restriccionRepository;
        this.docenteAsignaturaRepository = docenteAsignaturaRepository;
        this.restriccionService = restriccionService;
        this.docenteAsignaturaService = docenteAsignaturaService;
    }

    @Override
    public int create(DocenteDTO dto) {
        validarCampos(dto);
        Docente docente = new Docente();
        copiarCampos(dto, docente);
        docenteRepository.save(docente);
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocenteDTO> getAll() {
        return getAll(false);
    }

    @Transactional(readOnly = true)
    public List<DocenteDTO> getAll(boolean soloNombre) {
        return getAll(docenteId -> !soloNombre);
    }

    /** Lista los docentes con el detalle completo solo de aquellos que {@code veDetalle} permite. */
    @Transactional(readOnly = true)
    public List<DocenteDTO> getAll(Predicate<Long> veDetalle) {
        return docenteRepository.findAll().stream()
                .map(docente -> convertirADTO(docente, !veDetalle.test(docente.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public DocenteDTO getById(Long id, boolean soloNombre) {
        return convertirADTO(buscar(id), soloNombre);
    }

    @Override
    public int deleteById(Long id) {
        Docente docente = buscar(id);
        if (grupoRepository.existsByDocenteId(id)) {
            throw new OperacionNoPermitidaException("No se puede eliminar el docente porque tiene grupos asignados");
        }
        for (Usuario usuario : usuarioRepository.findByDocenteId(id)) {
            usuario.setDocente(null);
        }
        restriccionRepository.deleteByDocenteId(id);
        docenteAsignaturaRepository.deleteByDocenteId(id);
        docenteRepository.delete(docente);
        return 0;
    }

    @Override
    public int updateById(Long id, DocenteDTO dto) {
        Docente docente = buscar(id);
        validarCampos(dto);
        copiarCampos(dto, docente);
        docenteRepository.save(docente);
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return docenteRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exist(Long id) {
        return docenteRepository.existsById(id);
    }

    private Docente buscar(Long id) {
        return docenteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el docente con id " + id));
    }

    private void validarCampos(DocenteDTO dto) {
        if (dto == null) {
            throw new DatoInvalidoException("Los datos del docente son obligatorios");
        }
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new DatoInvalidoException("El nombre del docente es obligatorio");
        }
        if (dto.getNombre().trim().length() > LONGITUD_MAXIMA_NOMBRE) {
            throw new DatoInvalidoException(
                    "El nombre del docente admite hasta " + LONGITUD_MAXIMA_NOMBRE + " caracteres");
        }
        if (dto.getVinculacion() == null) {
            throw new DatoInvalidoException("El tipo de vinculación del docente es obligatorio");
        }
    }

    private void copiarCampos(DocenteDTO dto, Docente docente) {
        docente.setNombre(dto.getNombre().trim());
        docente.setVinculacion(dto.getVinculacion());
    }

    private DocenteDTO convertirADTO(Docente docente, boolean soloNombre) {
        if (soloNombre) {
            return new DocenteDTO(docente.getId(), docente.getNombre());
        }
        List<RestriccionHorariaDocenteDTO> restricciones = restriccionService.getByDocenteId(docente.getId());
        List<AsignaturaDTO> asignaturas = docenteAsignaturaService.getAsignaturasDeDocente(docente.getId());
        return new DocenteDTO(docente.getId(), docente.getNombre(), docente.getVinculacion(), restricciones,
                asignaturas);
    }
}
