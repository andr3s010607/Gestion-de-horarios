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

import co.edu.unbosque.gestorhorarios.dto.UsuarioDTO;
import co.edu.unbosque.gestorhorarios.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody UsuarioDTO dto) {
        int status = usuarioService.create(dto);
        if (status == 0) {
            return new ResponseEntity<>("Usuario creado correctamente", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Error al crear Usuario", HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        return new ResponseEntity<>(usuarioService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        UsuarioDTO dto = usuarioService.getById(id);
        if (dto != null) {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        int status = usuarioService.updateById(id, dto);
        if (status == 0) {
            return new ResponseEntity<>("Usuario actualizado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error al actualizar Usuario", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int status = usuarioService.deleteById(id);
        if (status == 0) {
            return new ResponseEntity<>("Usuario eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return new ResponseEntity<>(usuarioService.count(), HttpStatus.OK);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return new ResponseEntity<>(usuarioService.exist(id), HttpStatus.OK);
    }
}

