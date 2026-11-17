package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.GrupoDTO;
import co.edu.unbosque.gestorhorarios.service.GrupoService;
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
@RequestMapping("/grupos")
public class GrupoController {

    private final GrupoService grupoService;

    public GrupoController(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody GrupoDTO dto) {
        grupoService.create(dto);
        return new ResponseEntity<>("Grupo creado correctamente con sus sesiones sin ubicar", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GrupoDTO>> obtenerTodos() {
        return ResponseEntity.ok(grupoService.getAll());
    }

    @GetMapping("/horario/{horarioId}")
    public ResponseEntity<List<GrupoDTO>> obtenerPorHorario(@PathVariable Long horarioId) {
        return ResponseEntity.ok(grupoService.getByHorarioId(horarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(grupoService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody GrupoDTO dto) {
        grupoService.updateById(id, dto);
        return ResponseEntity.ok("Grupo actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        grupoService.deleteById(id);
        return ResponseEntity.ok("Grupo eliminado correctamente");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return ResponseEntity.ok(grupoService.count());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return ResponseEntity.ok(grupoService.exist(id));
    }
}
