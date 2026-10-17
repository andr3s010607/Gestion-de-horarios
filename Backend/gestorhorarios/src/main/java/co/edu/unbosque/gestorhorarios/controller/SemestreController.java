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

import co.edu.unbosque.gestorhorarios.dto.SemestreDTO;
import co.edu.unbosque.gestorhorarios.service.SemestreService;

@RestController
@RequestMapping("/semestres")
public class SemestreController {

    private final SemestreService semestreService;

    public SemestreController(SemestreService semestreService) {
        this.semestreService = semestreService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody SemestreDTO dto) {
        int status = semestreService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("Semestre creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear Semestre", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<SemestreDTO>> obtenerTodos() {
        return new ResponseEntity<>(semestreService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SemestreDTO> obtenerPorId(@PathVariable Long id) {
        SemestreDTO dto = semestreService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody SemestreDTO dto) {
        int status = semestreService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("Semestre actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar Semestre", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = semestreService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("Semestre eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Semestre no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(semestreService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(semestreService.exist(id), HttpStatus.OK);
    }
}

