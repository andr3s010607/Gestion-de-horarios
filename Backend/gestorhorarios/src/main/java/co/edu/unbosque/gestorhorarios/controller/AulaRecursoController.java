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

import co.edu.unbosque.gestorhorarios.dto.AulaRecursoDTO;
import co.edu.unbosque.gestorhorarios.service.AulaRecursoService;

@RestController
@RequestMapping("/aulas-recursos")
public class AulaRecursoController {

    private final AulaRecursoService aulaRecursoService;

    public AulaRecursoController(AulaRecursoService aulaRecursoService) {
        this.aulaRecursoService = aulaRecursoService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody AulaRecursoDTO dto) {
        int status = aulaRecursoService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("AulaRecurso creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear AulaRecurso", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<AulaRecursoDTO>> obtenerTodos() {
        return new ResponseEntity<>(aulaRecursoService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AulaRecursoDTO> obtenerPorId(@PathVariable Long id) {
        AulaRecursoDTO dto = aulaRecursoService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody AulaRecursoDTO dto) {
        int status = aulaRecursoService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("AulaRecurso actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar AulaRecurso", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = aulaRecursoService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("AulaRecurso eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("AulaRecurso no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(aulaRecursoService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(aulaRecursoService.exist(id), HttpStatus.OK);
    }
}

