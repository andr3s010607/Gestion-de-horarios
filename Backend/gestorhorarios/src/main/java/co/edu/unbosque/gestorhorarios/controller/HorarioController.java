package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.CuadriculaHorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.DemandaGrupoDTO;
import co.edu.unbosque.gestorhorarios.dto.HorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.dto.ResultadoGeneracionHorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.SesionClaseDTO;
import co.edu.unbosque.gestorhorarios.service.ExportacionService;
import co.edu.unbosque.gestorhorarios.service.HorarioService;
import co.edu.unbosque.gestorhorarios.service.PreseleccionService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/horarios")
public class HorarioController {

    private static final MediaType TIPO_CSV = new MediaType("text", "csv", StandardCharsets.UTF_8);

    private final HorarioService horarioService;
    private final ExportacionService exportacionService;
    private final PreseleccionService preseleccionService;

    public HorarioController(HorarioService horarioService, ExportacionService exportacionService,
            PreseleccionService preseleccionService) {
        this.horarioService = horarioService;
        this.exportacionService = exportacionService;
        this.preseleccionService = preseleccionService;
    }

    @PostMapping("/{id}/preseleccion/abrir")
    public ResponseEntity<HorarioDTO> abrirPreseleccion(@PathVariable Long id) {
        return ResponseEntity.ok(preseleccionService.abrir(id));
    }

    @PostMapping("/{id}/preseleccion/cerrar")
    public ResponseEntity<HorarioDTO> cerrarPreseleccion(@PathVariable Long id) {
        return ResponseEntity.ok(preseleccionService.cerrar(id));
    }

    @GetMapping("/{id}/demanda")
    public ResponseEntity<List<DemandaGrupoDTO>> consultarDemanda(@PathVariable Long id) {
        return ResponseEntity.ok(preseleccionService.demanda(id));
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody HorarioDTO dto) {
        horarioService.create(dto);
        return new ResponseEntity<>("Horario creado correctamente", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HorarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(horarioService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody HorarioDTO dto) {
        horarioService.updateById(id, dto);
        return ResponseEntity.ok("Horario actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        horarioService.deleteById(id);
        return ResponseEntity.ok("Horario eliminado correctamente");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return ResponseEntity.ok(horarioService.count());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.exist(id));
    }

    @PostMapping("/{id}/generar")
    public ResponseEntity<ResultadoGeneracionHorarioDTO> generarPropuesta(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.generarPropuesta(id));
    }

    @GetMapping("/{id}/validar")
    public ResponseEntity<List<IncidenciaDTO>> validar(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.validarHorario(id));
    }

    @GetMapping("/{id}/cuadricula")
    public ResponseEntity<CuadriculaHorarioDTO> obtenerCuadricula(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.obtenerCuadricula(id));
    }

    @GetMapping("/{id}/sesiones")
    public ResponseEntity<List<SesionClaseDTO>> obtenerSesiones(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.getSesiones(id));
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<HorarioDTO> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.aprobar(id));
    }

    @PostMapping("/{id}/devolver-a-borrador")
    public ResponseEntity<HorarioDTO> devolverABorrador(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.devolverABorrador(id));
    }

    @GetMapping("/{id}/docente/{docenteId}")
    public ResponseEntity<List<SesionClaseDTO>> consultarPorDocente(@PathVariable Long id,
            @PathVariable Long docenteId) {
        return ResponseEntity.ok(horarioService.consultarPorDocente(id, docenteId));
    }

    @GetMapping("/{id}/aula/{aulaId}")
    public ResponseEntity<List<SesionClaseDTO>> consultarPorAula(@PathVariable Long id, @PathVariable Long aulaId) {
        return ResponseEntity.ok(horarioService.consultarPorAula(id, aulaId));
    }

    @GetMapping("/{id}/asignatura/{asignaturaId}")
    public ResponseEntity<List<SesionClaseDTO>> consultarPorAsignatura(@PathVariable Long id,
            @PathVariable Long asignaturaId) {
        return ResponseEntity.ok(horarioService.consultarPorAsignatura(id, asignaturaId));
    }

    @GetMapping("/{id}/exportar")
    public ResponseEntity<byte[]> exportar(@PathVariable Long id) {
        return responderCsv(horarioService.consultarHorarioCompleto(id), "horario-" + id);
    }

    @GetMapping("/{id}/docente/{docenteId}/exportar")
    public ResponseEntity<byte[]> exportarPorDocente(@PathVariable Long id, @PathVariable Long docenteId) {
        return responderCsv(horarioService.consultarPorDocente(id, docenteId),
                "horario-" + id + "-docente-" + docenteId);
    }

    @GetMapping("/{id}/aula/{aulaId}/exportar")
    public ResponseEntity<byte[]> exportarPorAula(@PathVariable Long id, @PathVariable Long aulaId) {
        return responderCsv(horarioService.consultarPorAula(id, aulaId), "horario-" + id + "-aula-" + aulaId);
    }

    @GetMapping("/{id}/asignatura/{asignaturaId}/exportar")
    public ResponseEntity<byte[]> exportarPorAsignatura(@PathVariable Long id, @PathVariable Long asignaturaId) {
        return responderCsv(horarioService.consultarPorAsignatura(id, asignaturaId),
                "horario-" + id + "-asignatura-" + asignaturaId);
    }

    private ResponseEntity<byte[]> responderCsv(List<SesionClaseDTO> sesiones, String nombreArchivo) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo + ".csv")
                .contentType(TIPO_CSV)
                .body(exportacionService.exportarCsv(sesiones));
    }
}
