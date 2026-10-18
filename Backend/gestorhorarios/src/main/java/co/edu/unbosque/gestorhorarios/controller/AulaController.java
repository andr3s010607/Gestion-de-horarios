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

import co.edu.unbosque.gestorhorarios.dto.AulaDTO;
import co.edu.unbosque.gestorhorarios.service.AulaService;

@RestController
@RequestMapping("/aulas")
public class AulaController {

    private final AulaService aulaService;

    public AulaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody AulaDTO dto) {
        int status = aulaService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("Aula creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear Aula", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<AulaDTO>> obtenerTodos() {
        return new ResponseEntity<>(aulaService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<AulaDTO> obtenerPorCodigo(@PathVariable String codigo) {
        AulaDTO dto = aulaService.getByCodigo(codigo);
        return dto == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping("/codigo/{codigo}")
    public ResponseEntity<String> actualizarPorCodigo(@PathVariable String codigo, @RequestBody AulaDTO dto) {
        return aulaService.updateByCodigo(codigo, dto) == 0
                ? new ResponseEntity<>("Aula actualizada correctamente", HttpStatus.OK)
                : new ResponseEntity<>("Error al actualizar Aula", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/codigo/{codigo}")
    public ResponseEntity<String> eliminarPorCodigo(@PathVariable String codigo) {
        return aulaService.deleteByCodigo(codigo) == 0
                ? new ResponseEntity<>("Aula eliminada correctamente", HttpStatus.OK)
                : new ResponseEntity<>("Aula no encontrada", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AulaDTO> obtenerPorId(@PathVariable Long id) {
        AulaDTO dto = aulaService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody AulaDTO dto) {
        int status = aulaService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("Aula actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar Aula", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = aulaService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("Aula eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Aula no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(aulaService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(aulaService.exist(id), HttpStatus.OK);
    }
}

