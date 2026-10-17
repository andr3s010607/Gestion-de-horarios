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

import co.edu.unbosque.gestorhorarios.dto.SesionClaseDTO;
import co.edu.unbosque.gestorhorarios.service.SesionClaseService;

@RestController
@RequestMapping("/sesiones-clase")
public class SesionClaseController {

    private final SesionClaseService sesionClaseService;

    public SesionClaseController(SesionClaseService sesionClaseService) {
        this.sesionClaseService = sesionClaseService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody SesionClaseDTO dto) {
        List<String> errores = sesionClaseService.validarSesion(dto, null);
        if (!errores.isEmpty()) {
            return new ResponseEntity<>(String.join(". ", errores), HttpStatus.BAD_REQUEST);
        }
        int status = sesionClaseService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("SesionClase creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear SesionClase", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<SesionClaseDTO>> obtenerTodos() {
        return new ResponseEntity<>(sesionClaseService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SesionClaseDTO> obtenerPorId(@PathVariable Long id) {
        SesionClaseDTO dto = sesionClaseService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody SesionClaseDTO dto) {
        List<String> errores = sesionClaseService.validarSesion(dto, id);
        if (!errores.isEmpty()) {
            return new ResponseEntity<>(String.join(". ", errores), HttpStatus.BAD_REQUEST);
        }
        int status = sesionClaseService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("SesionClase actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar SesionClase", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = sesionClaseService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("SesionClase eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("SesionClase no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(sesionClaseService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(sesionClaseService.exist(id), HttpStatus.OK);
    }
}

