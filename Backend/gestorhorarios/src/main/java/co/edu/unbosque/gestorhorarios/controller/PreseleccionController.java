package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.AsignaturaPreseleccionDTO;
import co.edu.unbosque.gestorhorarios.dto.HorarioDTO;
import co.edu.unbosque.gestorhorarios.dto.IncidenciaDTO;
import co.edu.unbosque.gestorhorarios.dto.PreseleccionDTO;
import co.edu.unbosque.gestorhorarios.service.ExportacionService;
import co.edu.unbosque.gestorhorarios.service.PreseleccionService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Preselección de grupos del estudiante (RF-32 a RF-34). Solo el rol Estudiante puede usarla (RF-02). */
@RestController
@RequestMapping("/preselecciones")
public class PreseleccionController {

    private static final MediaType TIPO_CSV = new MediaType("text", "csv", StandardCharsets.UTF_8);

    private final PreseleccionService preseleccionService;
    private final ExportacionService exportacionService;

    public PreseleccionController(PreseleccionService preseleccionService, ExportacionService exportacionService) {
        this.preseleccionService = preseleccionService;
        this.exportacionService = exportacionService;
    }

    @GetMapping("/horarios")
    public ResponseEntity<List<HorarioDTO>> horariosVisibles(Authentication autenticacion) {
        return ResponseEntity.ok(preseleccionService.horariosVisibles(autenticacion));
    }

    @GetMapping("/horario/{horarioId}/grupos")
    public ResponseEntity<List<AsignaturaPreseleccionDTO>> gruposDisponibles(@PathVariable Long horarioId,
            Authentication autenticacion) {
        return ResponseEntity.ok(preseleccionService.gruposDisponibles(autenticacion, horarioId));
    }

    @PostMapping("/horario/{horarioId}/borrador")
    public ResponseEntity<List<IncidenciaDTO>> revisarBorrador(@PathVariable Long horarioId,
            @RequestBody PreseleccionDTO borrador) {
        return ResponseEntity.ok(preseleccionService.revisarBorrador(horarioId, borrador));
    }

    @GetMapping("/horario/{horarioId}")
    public ResponseEntity<PreseleccionDTO> miPreseleccion(@PathVariable Long horarioId,
            Authentication autenticacion) {
        return ResponseEntity.ok(preseleccionService.miPreseleccion(autenticacion, horarioId));
    }

    @GetMapping("/horario/{horarioId}/exportar")
    public ResponseEntity<byte[]> exportar(@PathVariable Long horarioId, Authentication autenticacion) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mi-preseleccion-" + horarioId + ".csv")
                .contentType(TIPO_CSV)
                .body(exportacionService.exportarCsv(
                        preseleccionService.sesionesDeMiPreseleccion(autenticacion, horarioId)));
    }

    @PutMapping("/horario/{horarioId}")
    public ResponseEntity<PreseleccionDTO> confirmar(@PathVariable Long horarioId,
            @RequestBody PreseleccionDTO borrador, Authentication autenticacion) {
        return ResponseEntity.ok(preseleccionService.confirmar(autenticacion, horarioId, borrador));
    }
}
