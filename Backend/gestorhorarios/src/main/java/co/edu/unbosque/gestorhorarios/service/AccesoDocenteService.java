package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.entity.Usuario;
import co.edu.unbosque.gestorhorarios.enums.RolUsuario;
import co.edu.unbosque.gestorhorarios.exception.AccesoDenegadoException;
import co.edu.unbosque.gestorhorarios.repository.UsuarioRepository;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Limita los datos personales de los docentes (Ley 1581 de 2012, RNF-07): el
 * administrador ve el detalle de todos, un docente solo el suyo y un
 * estudiante solo los nombres.
 */
@Service
@Transactional(readOnly = true)
public class AccesoDocenteService {

    private final UsuarioRepository usuarioRepository;

    public AccesoDocenteService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean puedeVerDetalle(Authentication autenticacion, Long docenteId) {
        Usuario usuario = usuarioActual(autenticacion);
        if (usuario.getRol() == RolUsuario.ADMINISTRADOR) {
            return true;
        }
        return usuario.getRol() == RolUsuario.DOCENTE && usuario.getDocente() != null
                && Objects.equals(usuario.getDocente().getId(), docenteId);
    }

    public void exigirDetalle(Authentication autenticacion, Long docenteId) {
        if (!puedeVerDetalle(autenticacion, docenteId)) {
            throw new AccesoDenegadoException("Solo puedes consultar la información de tu propio perfil docente");
        }
    }

    private Usuario usuarioActual(Authentication autenticacion) {
        if (autenticacion == null) {
            throw new AccesoDenegadoException("Debes iniciar sesión");
        }
        return usuarioRepository.findByNombreUsuario(autenticacion.getName())
                .orElseThrow(() -> new AccesoDenegadoException("Debes iniciar sesión"));
    }
}
