package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.LoginDTO;
import co.edu.unbosque.gestorhorarios.dto.RespuestaAutenticacionDTO;
import co.edu.unbosque.gestorhorarios.entity.Usuario;
import co.edu.unbosque.gestorhorarios.exception.CredencialesInvalidasException;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.repository.UsuarioRepository;
import co.edu.unbosque.gestorhorarios.security.JwtUtil;
import co.edu.unbosque.gestorhorarios.exception.DemasiadosIntentosException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacionService {

    private static final String TIPO_TOKEN = "Bearer";
    private static final int MAXIMO_INTENTOS_FALLIDOS = 5;
    private static final Duration DURACION_BLOQUEO = Duration.ofMinutes(5);

    /** Intentos fallidos por nombre de usuario, para frenar ataques de fuerza bruta. */
    private final Map<String, IntentosFallidos> intentosFallidos = new ConcurrentHashMap<>();

    private record IntentosFallidos(int cantidad, Instant bloqueadoHasta) {
    }

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public AutenticacionService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
            JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional(readOnly = true)
    public RespuestaAutenticacionDTO iniciarSesion(LoginDTO dto) {
        if (dto == null || dto.getNombreUsuario() == null || dto.getNombreUsuario().isBlank()
                || dto.getContrasena() == null || dto.getContrasena().isBlank()) {
            throw new DatoInvalidoException("El nombre de usuario y la contraseña son obligatorios");
        }
        String clave = dto.getNombreUsuario().trim().toLowerCase(Locale.ROOT);
        exigirQueNoEsteBloqueado(clave);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getNombreUsuario().trim(), dto.getContrasena()));
        } catch (AuthenticationException excepcion) {
            registrarFallo(clave);
            throw new CredencialesInvalidasException("El nombre de usuario o la contraseña no son correctos");
        }
        intentosFallidos.remove(clave);
        Usuario usuario = usuarioRepository.findByNombreUsuario(dto.getNombreUsuario().trim())
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "El nombre de usuario o la contraseña no son correctos"));
        return new RespuestaAutenticacionDTO(jwtUtil.generateToken(usuario), TIPO_TOKEN, usuario.getId(),
                usuario.getNombreUsuario(), usuario.getRol(),
                usuario.getDocente() == null ? null : usuario.getDocente().getId());
    }

    private void exigirQueNoEsteBloqueado(String clave) {
        IntentosFallidos intentos = intentosFallidos.get(clave);
        if (intentos == null || intentos.bloqueadoHasta() == null) {
            return;
        }
        Duration restante = Duration.between(Instant.now(), intentos.bloqueadoHasta());
        if (restante.isNegative() || restante.isZero()) {
            intentosFallidos.remove(clave);
            return;
        }
        long minutos = Math.max(1, (restante.getSeconds() + 59) / 60);
        throw new DemasiadosIntentosException("Demasiados intentos fallidos. Intenta de nuevo en " + minutos
                + (minutos == 1 ? " minuto" : " minutos"));
    }

    private void registrarFallo(String clave) {
        intentosFallidos.compute(clave, (usuario, anteriores) -> {
            int cantidad = (anteriores == null ? 0 : anteriores.cantidad()) + 1;
            Instant bloqueo = cantidad >= MAXIMO_INTENTOS_FALLIDOS ? Instant.now().plus(DURACION_BLOQUEO) : null;
            return new IntentosFallidos(bloqueo == null ? cantidad : 0, bloqueo);
        });
    }
}
