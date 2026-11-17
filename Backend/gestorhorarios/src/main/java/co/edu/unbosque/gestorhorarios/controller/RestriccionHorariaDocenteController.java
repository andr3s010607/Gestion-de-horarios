package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.RestriccionHorariaDocenteDTO;
import co.edu.unbosque.gestorhorarios.service.AccesoDocenteService;
import co.edu.unbosque.gestorhorarios.service.RestriccionHorariaDocenteService;
import org.springframework.security.core.Authentication;
import java.util.List;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/restricciones-horarias-docente")
public class RestriccionHorariaDocenteController {

    private final RestriccionHorariaDocenteService restriccionService;
    private final AccesoDocenteService accesoDocente;

    public RestriccionHorariaDocenteController(RestriccionHorariaDocenteService restriccionService,
            AccesoDocenteService accesoDocente) {
        this.restriccionService = restriccionService;
        this.accesoDocente = accesoDocente;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody RestriccionHorariaDocenteDTO dto) {
        restriccionService.create(dto);
        return new ResponseEntity<>("Restricción de horario creada correctamente", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RestriccionHorariaDocenteDTO>> obtenerTodas(Authentication autenticacion) {
        return ResponseEntity.ok(restriccionService.getAll().stream()
                .filter(restriccion -> accesoDocente.puedeVerDetalle(autenticacion, restriccion.getDocenteId()))
                .toList());
    }

    @GetMapping("/docente/{docenteId}")
    public ResponseEntity<List<RestriccionHorariaDocenteDTO>> obtenerPorDocente(@PathVariable Long docenteId,
            Authentication autenticacion) {
        accesoDocente.exigirDetalle(autenticacion, docenteId);
        return ResponseEntity.ok(restriccionService.getByDocenteId(docenteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestriccionHorariaDocenteDTO> obtenerPorId(@PathVariable Long id,
            Authentication autenticacion) {
        RestriccionHorariaDocenteDTO restriccion = restriccionService.getById(id);
        accesoDocente.exigirDetalle(autenticacion, restriccion.getDocenteId());
        return ResponseEntity.ok(restriccion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody RestriccionHorariaDocenteDTO dto) {
        restriccionService.updateById(id, dto);
        return ResponseEntity.ok("Restricción de horario actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        restriccionService.deleteById(id);
        return ResponseEntity.ok("Restricción de horario eliminada correctamente");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return ResponseEntity.ok(restriccionService.count());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return ResponseEntity.ok(restriccionService.exist(id));
    }
}
