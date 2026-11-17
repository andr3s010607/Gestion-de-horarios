package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.AsignaturaDTO;
import co.edu.unbosque.gestorhorarios.service.AsignaturaService;
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
@RequestMapping("/asignaturas")
public class AsignaturaController {

    private final AsignaturaService asignaturaService;

    public AsignaturaController(AsignaturaService asignaturaService) {
        this.asignaturaService = asignaturaService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody AsignaturaDTO dto) {
        asignaturaService.create(dto);
        return new ResponseEntity<>("Asignatura creada correctamente", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AsignaturaDTO>> obtenerTodas() {
        return ResponseEntity.ok(asignaturaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignaturaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(asignaturaService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody AsignaturaDTO dto) {
        asignaturaService.updateById(id, dto);
        return ResponseEntity.ok("Asignatura actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        asignaturaService.deleteById(id);
        return ResponseEntity.ok("Asignatura eliminada correctamente");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return ResponseEntity.ok(asignaturaService.count());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return ResponseEntity.ok(asignaturaService.exist(id));
    }
}
