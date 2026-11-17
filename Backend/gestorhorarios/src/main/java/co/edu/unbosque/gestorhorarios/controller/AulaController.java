package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.AulaDTO;
import co.edu.unbosque.gestorhorarios.service.AulaService;
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
@RequestMapping("/aulas")
public class AulaController {

    private final AulaService aulaService;

    public AulaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody AulaDTO dto) {
        aulaService.create(dto);
        return new ResponseEntity<>("Aula creada correctamente", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AulaDTO>> obtenerTodas() {
        return ResponseEntity.ok(aulaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AulaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(aulaService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody AulaDTO dto) {
        aulaService.updateById(id, dto);
        return ResponseEntity.ok("Aula actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        aulaService.deleteById(id);
        return ResponseEntity.ok("Aula eliminada correctamente");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return ResponseEntity.ok(aulaService.count());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return ResponseEntity.ok(aulaService.exist(id));
    }
}
