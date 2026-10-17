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

import co.edu.unbosque.gestorhorarios.dto.GrupoDTO;
import co.edu.unbosque.gestorhorarios.service.GrupoService;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    private final GrupoService grupoService;

    public GrupoController(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody GrupoDTO dto) {
        int status = grupoService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("Grupo creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear Grupo", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<GrupoDTO>> obtenerTodos() {
        return new ResponseEntity<>(grupoService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoDTO> obtenerPorId(@PathVariable Long id) {
        GrupoDTO dto = grupoService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody GrupoDTO dto) {
        int status = grupoService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("Grupo actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar Grupo", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = grupoService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("Grupo eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Grupo no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(grupoService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(grupoService.exist(id), HttpStatus.OK);
    }
}

