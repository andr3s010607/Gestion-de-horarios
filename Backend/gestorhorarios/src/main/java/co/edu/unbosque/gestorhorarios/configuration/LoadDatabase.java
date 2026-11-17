package co.edu.unbosque.gestorhorarios.configuration;

import co.edu.unbosque.gestorhorarios.entity.ParametroSistema;
import co.edu.unbosque.gestorhorarios.entity.Usuario;
import co.edu.unbosque.gestorhorarios.enums.RolUsuario;
import co.edu.unbosque.gestorhorarios.repository.ParametroSistemaRepository;
import co.edu.unbosque.gestorhorarios.repository.UsuarioRepository;
import co.edu.unbosque.gestorhorarios.service.ReglaHorarioService;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class LoadDatabase {

    @Value("${app.admin.nombre-usuario}")
    private String nombreUsuarioAdministrador;

    @Value("${app.admin.contrasena}")
    private String contrasenaAdministrador;

    @Value("${app.parametros.hora-inicio-lunes-viernes}")
    private String horaInicioLunesViernes;

    @Value("${app.parametros.hora-fin-lunes-viernes}")
    private String horaFinLunesViernes;

    @Value("${app.parametros.hora-fin-sabado}")
    private String horaFinSabado;

    @Value("${app.parametros.hora-inicio-pausa}")
    private String horaInicioPausa;

    @Value("${app.parametros.hora-fin-pausa}")
    private String horaFinPausa;

    @Value("${app.parametros.duracion-sesion-horas}")
    private int duracionSesionHoras;

    @Value("${app.parametros.cupo-maximo}")
    private int cupoMaximo;

    @Value("${app.parametros.tolerancia-cupo-porcentaje}")
    private int toleranciaCupoPorcentaje;

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            ParametroSistemaRepository parametroSistemaRepository) {
        return args -> {
            crearAdministradorSiNoExiste(usuarioRepository, passwordEncoder);
            crearHoraSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_INICIO_LUNES_VIERNES,
                    horaInicioLunesViernes);
            crearHoraSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_FIN_LUNES_VIERNES,
                    horaFinLunesViernes);
            crearHoraSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_FIN_SABADO, horaFinSabado);
            crearHoraSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_INICIO_PAUSA, horaInicioPausa);
            crearHoraSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_FIN_PAUSA, horaFinPausa);
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.DURACION_SESION_HORAS,
                    duracionSesionHoras);
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.CUPO_MAXIMO, cupoMaximo);
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.TOLERANCIA_CUPO_PORCENTAJE,
                    toleranciaCupoPorcentaje);
        };
    }

    private void crearAdministradorSiNoExiste(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        if (usuarioRepository.existsByNombreUsuario(nombreUsuarioAdministrador)) {
            return;
        }
        Usuario administrador = new Usuario();
        administrador.setNombreUsuario(nombreUsuarioAdministrador);
        administrador.setHashContrasena(passwordEncoder.encode(contrasenaAdministrador));
        administrador.setRol(RolUsuario.ADMINISTRADOR);
        usuarioRepository.save(administrador);
    }

    private void crearHoraSiNoExiste(ParametroSistemaRepository parametroSistemaRepository, String clave,
            String hora) {
        crearParametroSiNoExiste(parametroSistemaRepository, clave,
                ReglaHorarioService.convertirAMinutos(LocalTime.parse(hora)));
    }

    private void crearParametroSiNoExiste(ParametroSistemaRepository parametroSistemaRepository, String clave,
            int valor) {
        if (!parametroSistemaRepository.existsById(clave)) {
            parametroSistemaRepository.save(new ParametroSistema(clave, String.valueOf(valor)));
        }
    }
}
