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

import co.edu.unbosque.gestorhorarios.dto.AsignaturaDTO;
import co.edu.unbosque.gestorhorarios.service.AsignaturaService;

@RestController
@RequestMapping("/asignaturas")
public class AsignaturaController {

    private final AsignaturaService asignaturaService;

    public AsignaturaController(AsignaturaService asignaturaService) {
        this.asignaturaService = asignaturaService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody AsignaturaDTO dto) {
        int status = asignaturaService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("Asignatura creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear Asignatura", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<AsignaturaDTO>> obtenerTodos() {
        return new ResponseEntity<>(asignaturaService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignaturaDTO> obtenerPorId(@PathVariable Long id) {
        AsignaturaDTO dto = asignaturaService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody AsignaturaDTO dto) {
        int status = asignaturaService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("Asignatura actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar Asignatura", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = asignaturaService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("Asignatura eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Asignatura no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(asignaturaService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(asignaturaService.exist(id), HttpStatus.OK);
    }
}

