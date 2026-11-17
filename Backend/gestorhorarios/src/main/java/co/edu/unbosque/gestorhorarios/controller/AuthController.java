package co.edu.unbosque.gestorhorarios.controller;

import co.edu.unbosque.gestorhorarios.dto.LoginDTO;
import co.edu.unbosque.gestorhorarios.dto.RespuestaAutenticacionDTO;
import co.edu.unbosque.gestorhorarios.service.AutenticacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AutenticacionService autenticacionService;

    public AuthController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping("/login")
    public ResponseEntity<RespuestaAutenticacionDTO> login(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(autenticacionService.iniciarSesion(loginDTO));
    }
}
