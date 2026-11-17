package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.AsignaturaDTO;
import co.edu.unbosque.gestorhorarios.dto.DocenteAsignaturaDTO;
import co.edu.unbosque.gestorhorarios.entity.Asignatura;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.entity.DocenteAsignatura;
import co.edu.unbosque.gestorhorarios.entity.DocenteAsignaturaId;
import co.edu.unbosque.gestorhorarios.exception.DatoDuplicadoException;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.exception.RecursoNoEncontradoException;
import co.edu.unbosque.gestorhorarios.repository.AsignaturaRepository;
import co.edu.unbosque.gestorhorarios.repository.DocenteAsignaturaRepository;
import co.edu.unbosque.gestorhorarios.repository.DocenteRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DocenteAsignaturaService {

    private final DocenteAsignaturaRepository docenteAsignaturaRepository;
    private final DocenteRepository docenteRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final AsignaturaService asignaturaService;
    private final ProteccionHorarioAprobadoService proteccionHorarioAprobado;

    public DocenteAsignaturaService(DocenteAsignaturaRepository docenteAsignaturaRepository,
            DocenteRepository docenteRepository, AsignaturaRepository asignaturaRepository,
            AsignaturaService asignaturaService, ProteccionHorarioAprobadoService proteccionHorarioAprobado) {
        this.proteccionHorarioAprobado = proteccionHorarioAprobado;
        this.docenteAsignaturaRepository = docenteAsignaturaRepository;
        this.docenteRepository = docenteRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.asignaturaService = asignaturaService;
    }

    public void asociar(DocenteAsignaturaDTO dto) {
        if (dto == null || dto.getDocenteId() == null || dto.getAsignaturaId() == null) {
            throw new DatoInvalidoException("El docente y la asignatura son obligatorios");
        }
        Docente docente = docenteRepository.findById(dto.getDocenteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el docente con id " + dto.getDocenteId()));
        Asignatura asignatura = asignaturaRepository.findById(dto.getAsignaturaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la asignatura con id " + dto.getAsignaturaId()));
        if (docenteAsignaturaRepository.existsById(new DocenteAsignaturaId(docente.getId(), asignatura.getId()))) {
            throw new DatoDuplicadoException("El docente ya está habilitado para la asignatura");
        }
        docenteAsignaturaRepository.save(new DocenteAsignatura(docente, asignatura));
    }

    public void retirar(Long docenteId, Long asignaturaId) {
        DocenteAsignaturaId id = new DocenteAsignaturaId(docenteId, asignaturaId);
        DocenteAsignatura habilitacion = docenteAsignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El docente no está habilitado para la asignatura indicada"));
        proteccionHorarioAprobado.aplicarSinDanarHorariosAprobados(
                () -> docenteAsignaturaRepository.delete(habilitacion));
    }

    @Transactional(readOnly = true)
    public List<AsignaturaDTO> getAsignaturasDeDocente(Long docenteId) {
        if (!docenteRepository.existsById(docenteId)) {
            throw new RecursoNoEncontradoException("No existe el docente con id " + docenteId);
        }
        return docenteAsignaturaRepository.findByDocenteId(docenteId).stream()
                .map(DocenteAsignatura::getAsignatura)
                .sorted(Comparator.comparing(Asignatura::getCodigo))
                .map(asignaturaService::convertirADTO)
                .toList();
    }
}
