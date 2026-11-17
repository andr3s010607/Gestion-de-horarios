package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.MovimientoSesionDTO;
import co.edu.unbosque.gestorhorarios.dto.ResultadoMovimientoDTO;
import co.edu.unbosque.gestorhorarios.dto.SesionClaseDTO;
import co.edu.unbosque.gestorhorarios.service.HorarioService;
import co.edu.unbosque.gestorhorarios.service.SesionClaseService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sesiones-clase")
public class SesionClaseController {

    private final SesionClaseService sesionClaseService;
    private final HorarioService horarioService;

    public SesionClaseController(SesionClaseService sesionClaseService, HorarioService horarioService) {
        this.sesionClaseService = sesionClaseService;
        this.horarioService = horarioService;
    }

    @GetMapping
    public ResponseEntity<List<SesionClaseDTO>> obtenerTodas() {
        return ResponseEntity.ok(sesionClaseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SesionClaseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sesionClaseService.getById(id));
    }

    @PutMapping("/{id}/mover")
    public ResponseEntity<ResultadoMovimientoDTO> mover(@PathVariable Long id,
            @RequestBody MovimientoSesionDTO movimiento) {
        return ResponseEntity.ok(horarioService.moverSesion(id, movimiento));
    }

    @PutMapping("/{id}/devolver-a-bandeja")
    public ResponseEntity<SesionClaseDTO> devolverABandeja(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.devolverSesionABandeja(id));
    }
}
