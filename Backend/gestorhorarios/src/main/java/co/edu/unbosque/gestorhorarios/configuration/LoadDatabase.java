package co.edu.unbosque.gestorhorarios.configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.gestorhorarios.entity.Usuario;
import co.edu.unbosque.gestorhorarios.entity.ParametroSistema;
import co.edu.unbosque.gestorhorarios.enums.RolUsuario;
import co.edu.unbosque.gestorhorarios.enums.TipoDatoParametro;
import co.edu.unbosque.gestorhorarios.repository.ParametroSistemaRepository;
import co.edu.unbosque.gestorhorarios.repository.UsuarioRepository;
import co.edu.unbosque.gestorhorarios.service.ReglaHorarioService;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            ParametroSistemaRepository parametroSistemaRepository) {
        return args -> {
            Optional<Usuario> adminEncontrado = usuarioRepository.findByCorreo("admin@gestorhorarios.com");

            if (adminEncontrado.isPresent()) {
                Usuario adminExistente = adminEncontrado.get();
                if (!adminExistente.isActivo() || adminExistente.getRol() == null) {
                    adminExistente.setActivo(true);
                    adminExistente.setRol(RolUsuario.ADMINISTRADOR);
                    adminExistente.setContrasena(passwordEncoder.encode("Admin123*"));
                    usuarioRepository.save(adminExistente);
                    log.info("Administrador actualizado correctamente");
                } else {
                    log.info("El administrador ya existe");
                }
            } else {
                Usuario admin = new Usuario();
                admin.setNombreCompleto("Administrador");
                admin.setCorreo("admin@gestorhorarios.com");
                admin.setContrasena(passwordEncoder.encode("Admin123*"));
                admin.setRol(RolUsuario.ADMINISTRADOR);
                admin.setActivo(true);
                usuarioRepository.save(admin);
                log.info("Administrador creado correctamente");
            }

            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.CUPO_MAXIMO_GRUPO, "40",
                    TipoDatoParametro.ENTERO, "Máximo de estudiantes por grupo");
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.TOLERANCIA_CUPO_PORCENTAJE,
                    "10", TipoDatoParametro.DECIMAL, "Porcentaje de tolerancia del cupo");
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_INICIO_LUNES_VIERNES,
                    "07:00", TipoDatoParametro.HORA, "Inicio de jornada de lunes a viernes");
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_FIN_LUNES_VIERNES,
                    "22:00", TipoDatoParametro.HORA, "Fin de jornada de lunes a viernes");
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_INICIO_SABADO, "07:00",
                    TipoDatoParametro.HORA, "Inicio de jornada del sábado");
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_FIN_SABADO, "13:00",
                    TipoDatoParametro.HORA, "Fin de jornada del sábado");
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_INICIO_ALMUERZO, "12:00",
                    TipoDatoParametro.HORA, "Inicio de la franja no programable de almuerzo");
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.HORA_FIN_ALMUERZO, "13:00",
                    TipoDatoParametro.HORA, "Fin de la franja no programable de almuerzo");
            crearParametroSiNoExiste(parametroSistemaRepository, ReglaHorarioService.DURACION_SESION_MINUTOS, "120",
                    TipoDatoParametro.ENTERO, "Duración de cada sesión de clase en minutos");
        };
    }

    private void crearParametroSiNoExiste(ParametroSistemaRepository parametroSistemaRepository, String clave,
            String valor, TipoDatoParametro tipoDato, String descripcion) {
        if (parametroSistemaRepository.findByClave(clave).isEmpty()) {
            ParametroSistema parametro = new ParametroSistema();
            parametro.setClave(clave);
            parametro.setValor(valor);
            parametro.setTipoDato(tipoDato);
            parametro.setDescripcion(descripcion);
            parametro.setActivo(true);
            parametroSistemaRepository.save(parametro);
        }
    }
}
