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

import co.edu.unbosque.gestorhorarios.dto.DocenteDTO;
import co.edu.unbosque.gestorhorarios.service.DocenteService;

@RestController
@RequestMapping("/docentes")
public class DocenteController {

    private final DocenteService docenteService;

    public DocenteController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody DocenteDTO dto) {
        int status = docenteService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("Docente creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear Docente", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<DocenteDTO>> obtenerTodos() {
        return new ResponseEntity<>(docenteService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocenteDTO> obtenerPorId(@PathVariable Long id) {
        DocenteDTO dto = docenteService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody DocenteDTO dto) {
        int status = docenteService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("Docente actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar Docente", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = docenteService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("Docente eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Docente no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(docenteService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(docenteService.exist(id), HttpStatus.OK);
    }
}

