package co.edu.unbosque.gestorhorarios.controller;

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

import co.edu.unbosque.gestorhorarios.dto.RecursoAulaDTO;
import co.edu.unbosque.gestorhorarios.service.RecursoAulaService;

@RestController
@RequestMapping("/recursos-aula")
public class RecursoAulaController {

    private final RecursoAulaService recursoAulaService;

    public RecursoAulaController(RecursoAulaService recursoAulaService) {
        this.recursoAulaService = recursoAulaService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody RecursoAulaDTO dto) {
        int status = recursoAulaService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("RecursoAula creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear RecursoAula", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<RecursoAulaDTO>> obtenerTodos() {
        return new ResponseEntity<>(recursoAulaService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecursoAulaDTO> obtenerPorId(@PathVariable Long id) {
        RecursoAulaDTO dto = recursoAulaService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody RecursoAulaDTO dto) {
        int status = recursoAulaService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("RecursoAula actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar RecursoAula", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = recursoAulaService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("RecursoAula eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("RecursoAula no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(recursoAulaService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(recursoAulaService.exist(id), HttpStatus.OK);
    }
}

