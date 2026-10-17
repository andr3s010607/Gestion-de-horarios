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

import co.edu.unbosque.gestorhorarios.dto.RestriccionHorariaDocenteDTO;
import co.edu.unbosque.gestorhorarios.service.RestriccionHorariaDocenteService;

@RestController
@RequestMapping("/restricciones-horarias-docente")
public class RestriccionHorariaDocenteController {

    private final RestriccionHorariaDocenteService restriccionHorariaDocenteService;

    public RestriccionHorariaDocenteController(RestriccionHorariaDocenteService restriccionHorariaDocenteService) {
        this.restriccionHorariaDocenteService = restriccionHorariaDocenteService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody RestriccionHorariaDocenteDTO dto) {
        int status = restriccionHorariaDocenteService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("RestriccionHorariaDocente creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear RestriccionHorariaDocente", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<RestriccionHorariaDocenteDTO>> obtenerTodos() {
        return new ResponseEntity<>(restriccionHorariaDocenteService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestriccionHorariaDocenteDTO> obtenerPorId(@PathVariable Long id) {
        RestriccionHorariaDocenteDTO dto = restriccionHorariaDocenteService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody RestriccionHorariaDocenteDTO dto) {
        int status = restriccionHorariaDocenteService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("RestriccionHorariaDocente actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar RestriccionHorariaDocente", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = restriccionHorariaDocenteService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("RestriccionHorariaDocente eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("RestriccionHorariaDocente no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(restriccionHorariaDocenteService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(restriccionHorariaDocenteService.exist(id), HttpStatus.OK);
    }
}

