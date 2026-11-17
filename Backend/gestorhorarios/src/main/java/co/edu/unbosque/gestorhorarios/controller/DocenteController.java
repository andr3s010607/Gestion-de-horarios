package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.DocenteDTO;
import co.edu.unbosque.gestorhorarios.service.AccesoDocenteService;
import co.edu.unbosque.gestorhorarios.service.DocenteService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/docentes")
public class DocenteController {

    private final DocenteService docenteService;
    private final AccesoDocenteService accesoDocente;

    public DocenteController(DocenteService docenteService, AccesoDocenteService accesoDocente) {
        this.docenteService = docenteService;
        this.accesoDocente = accesoDocente;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody DocenteDTO dto) {
        docenteService.create(dto);
        return new ResponseEntity<>("Docente creado correctamente", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DocenteDTO>> obtenerTodos(Authentication autenticacion) {
        return ResponseEntity.ok(docenteService.getAll(id -> accesoDocente.puedeVerDetalle(autenticacion, id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocenteDTO> obtenerPorId(@PathVariable Long id, Authentication autenticacion) {
        return ResponseEntity.ok(docenteService.getById(id, !accesoDocente.puedeVerDetalle(autenticacion, id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody DocenteDTO dto) {
        docenteService.updateById(id, dto);
        return ResponseEntity.ok("Docente actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        docenteService.deleteById(id);
        return ResponseEntity.ok("Docente eliminado correctamente");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contar() {
        return ResponseEntity.ok(docenteService.count());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return ResponseEntity.ok(docenteService.exist(id));
    }

}
