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

import co.edu.unbosque.gestorhorarios.dto.DocenteAsignaturaDTO;
import co.edu.unbosque.gestorhorarios.service.DocenteAsignaturaService;

@RestController
@RequestMapping("/docentes-asignaturas")
public class DocenteAsignaturaController {

    private final DocenteAsignaturaService docenteAsignaturaService;

    public DocenteAsignaturaController(DocenteAsignaturaService docenteAsignaturaService) {
        this.docenteAsignaturaService = docenteAsignaturaService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody DocenteAsignaturaDTO dto) {
        int status = docenteAsignaturaService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("DocenteAsignatura creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear DocenteAsignatura", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<DocenteAsignaturaDTO>> obtenerTodos() {
        return new ResponseEntity<>(docenteAsignaturaService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocenteAsignaturaDTO> obtenerPorId(@PathVariable Long id) {
        DocenteAsignaturaDTO dto = docenteAsignaturaService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody DocenteAsignaturaDTO dto) {
        int status = docenteAsignaturaService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("DocenteAsignatura actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar DocenteAsignatura", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = docenteAsignaturaService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("DocenteAsignatura eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("DocenteAsignatura no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(docenteAsignaturaService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(docenteAsignaturaService.exist(id), HttpStatus.OK);
    }
}

