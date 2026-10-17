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

import co.edu.unbosque.gestorhorarios.dto.ParametroSistemaDTO;
import co.edu.unbosque.gestorhorarios.service.ParametroSistemaService;

@RestController
@RequestMapping("/parametros-sistema")
public class ParametroSistemaController {

    private final ParametroSistemaService parametroSistemaService;

    public ParametroSistemaController(ParametroSistemaService parametroSistemaService) {
        this.parametroSistemaService = parametroSistemaService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody ParametroSistemaDTO dto) {
        List<String> errores = parametroSistemaService.validar(dto);
        if (!errores.isEmpty()) {
            return new ResponseEntity<>(String.join(". ", errores), HttpStatus.BAD_REQUEST);
        }
        int status = parametroSistemaService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("ParametroSistema creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear ParametroSistema", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<ParametroSistemaDTO>> obtenerTodos() {
        return new ResponseEntity<>(parametroSistemaService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParametroSistemaDTO> obtenerPorId(@PathVariable Long id) {
        ParametroSistemaDTO dto = parametroSistemaService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody ParametroSistemaDTO dto) {
        List<String> errores = parametroSistemaService.validar(dto);
        if (!errores.isEmpty()) {
            return new ResponseEntity<>(String.join(". ", errores), HttpStatus.BAD_REQUEST);
        }
        int status = parametroSistemaService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("ParametroSistema actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar ParametroSistema", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = parametroSistemaService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("ParametroSistema eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("ParametroSistema no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(parametroSistemaService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(parametroSistemaService.exist(id), HttpStatus.OK);
    }
}

