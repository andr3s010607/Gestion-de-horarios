package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.AsignaturaDTO;
import co.edu.unbosque.gestorhorarios.dto.DocenteAsignaturaDTO;
import co.edu.unbosque.gestorhorarios.service.AccesoDocenteService;
import co.edu.unbosque.gestorhorarios.service.DocenteAsignaturaService;
import org.springframework.security.core.Authentication;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/docente-asignaturas")
public class DocenteAsignaturaController {

    private final DocenteAsignaturaService docenteAsignaturaService;
    private final AccesoDocenteService accesoDocente;

    public DocenteAsignaturaController(DocenteAsignaturaService docenteAsignaturaService,
            AccesoDocenteService accesoDocente) {
        this.docenteAsignaturaService = docenteAsignaturaService;
        this.accesoDocente = accesoDocente;
    }

    @PostMapping
    public ResponseEntity<String> asociar(@RequestBody DocenteAsignaturaDTO dto) {
        docenteAsignaturaService.asociar(dto);
        return new ResponseEntity<>("Asignatura habilitada para el docente correctamente", HttpStatus.CREATED);
    }

    @GetMapping("/docente/{docenteId}")
    public ResponseEntity<List<AsignaturaDTO>> obtenerAsignaturasDeDocente(@PathVariable Long docenteId,
            Authentication autenticacion) {
        accesoDocente.exigirDetalle(autenticacion, docenteId);
        return ResponseEntity.ok(docenteAsignaturaService.getAsignaturasDeDocente(docenteId));
    }

    @DeleteMapping("/docente/{docenteId}/asignatura/{asignaturaId}")
    public ResponseEntity<String> retirar(@PathVariable Long docenteId, @PathVariable Long asignaturaId) {
        docenteAsignaturaService.retirar(docenteId, asignaturaId);
        return ResponseEntity.ok("Habilitación retirada correctamente");
    }
}
