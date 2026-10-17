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

import co.edu.unbosque.gestorhorarios.dto.AsignaturaRecursoRequeridoDTO;
import co.edu.unbosque.gestorhorarios.service.AsignaturaRecursoRequeridoService;

@RestController
@RequestMapping("/asignaturas-recursos-requeridos")
public class AsignaturaRecursoRequeridoController {

    private final AsignaturaRecursoRequeridoService asignaturaRecursoRequeridoService;

    public AsignaturaRecursoRequeridoController(AsignaturaRecursoRequeridoService asignaturaRecursoRequeridoService) {
        this.asignaturaRecursoRequeridoService = asignaturaRecursoRequeridoService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody AsignaturaRecursoRequeridoDTO dto) {
        int status = asignaturaRecursoRequeridoService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("AsignaturaRecursoRequerido creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear AsignaturaRecursoRequerido", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<AsignaturaRecursoRequeridoDTO>> obtenerTodos() {
        return new ResponseEntity<>(asignaturaRecursoRequeridoService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignaturaRecursoRequeridoDTO> obtenerPorId(@PathVariable Long id) {
        AsignaturaRecursoRequeridoDTO dto = asignaturaRecursoRequeridoService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody AsignaturaRecursoRequeridoDTO dto) {
        int status = asignaturaRecursoRequeridoService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("AsignaturaRecursoRequerido actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar AsignaturaRecursoRequerido", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = asignaturaRecursoRequeridoService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("AsignaturaRecursoRequerido eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("AsignaturaRecursoRequerido no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(asignaturaRecursoRequeridoService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(asignaturaRecursoRequeridoService.exist(id), HttpStatus.OK);
    }
}

