package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.ParametroSistemaDTO;
import co.edu.unbosque.gestorhorarios.service.ParametroSistemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parametros-sistema")
public class ParametroSistemaController {

    private final ParametroSistemaService parametroSistemaService;

    public ParametroSistemaController(ParametroSistemaService parametroSistemaService) {
        this.parametroSistemaService = parametroSistemaService;
    }

    @GetMapping
    public ResponseEntity<ParametroSistemaDTO> obtener() {
        return ResponseEntity.ok(parametroSistemaService.getParametros());
    }

    @PutMapping
    public ResponseEntity<ParametroSistemaDTO> actualizar(@RequestBody ParametroSistemaDTO dto) {
        return ResponseEntity.ok(parametroSistemaService.actualizar(dto));
    }
}
