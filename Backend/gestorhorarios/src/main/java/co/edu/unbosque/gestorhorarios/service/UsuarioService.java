package co.edu.unbosque.gestorhorarios.service;

import co.edu.unbosque.gestorhorarios.dto.UsuarioDTO;
import co.edu.unbosque.gestorhorarios.entity.Docente;
import co.edu.unbosque.gestorhorarios.entity.Usuario;
import co.edu.unbosque.gestorhorarios.exception.DatoDuplicadoException;
import co.edu.unbosque.gestorhorarios.enums.RolUsuario;
import co.edu.unbosque.gestorhorarios.exception.DatoInvalidoException;
import co.edu.unbosque.gestorhorarios.exception.OperacionNoPermitidaException;
import co.edu.unbosque.gestorhorarios.exception.RecursoNoEncontradoException;
import co.edu.unbosque.gestorhorarios.repository.DocenteRepository;
import co.edu.unbosque.gestorhorarios.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService implements CRUDOperation<UsuarioDTO> {

    private static final int LONGITUD_MAXIMA_NOMBRE_USUARIO = 60;
    private static final int LONGITUD_MINIMA_CONTRASENA = 8;
    /** BCrypt solo usa los primeros 72 bytes y rechaza contraseñas más largas. */
    private static final int LONGITUD_MAXIMA_CONTRASENA_BYTES = 72;

    private final UsuarioRepository usuarioRepository;
    private final DocenteRepository docenteRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, DocenteRepository docenteRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.docenteRepository = docenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public int create(UsuarioDTO dto) {
        validarCampos(dto);
        if (dto.getContrasena() == null || dto.getContrasena().isBlank()) {
            throw new DatoInvalidoException("La contraseña es obligatoria");
        }
        if (usuarioRepository.existsByNombreUsuarioIgnoreCase(dto.getNombreUsuario().trim())) {
            throw new DatoDuplicadoException("Ya existe un usuario con el nombre " + dto.getNombreUsuario().trim());
        }
        Usuario usuario = new Usuario();
        copiarCampos(dto, usuario);
        usuarioRepository.save(usuario);
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> getAll() {
        return usuarioRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getById(Long id) {
        return convertirADTO(buscar(id));
    }

    @Override
    public int deleteById(Long id) {
        Usuario usuario = buscar(id);
        exigirOtroAdministrador(usuario, null);
        usuarioRepository.delete(usuario);
        return 0;
    }

    @Override
    public int updateById(Long id, UsuarioDTO dto) {
        Usuario usuario = buscar(id);
        validarCampos(dto);
        if (usuarioRepository.existsByNombreUsuarioIgnoreCaseAndIdNot(dto.getNombreUsuario().trim(), id)) {
            throw new DatoDuplicadoException("Ya existe un usuario con el nombre " + dto.getNombreUsuario().trim());
        }
        exigirOtroAdministrador(usuario, dto.getRol());
        copiarCampos(dto, usuario);
        usuarioRepository.save(usuario);
        return 0;
    }

    /**
     * Impide quedarse sin administradores: rechaza borrar o cambiar de rol al
     * último usuario con rol Administrador.
     */
    private void exigirOtroAdministrador(Usuario usuario, RolUsuario rolNuevo) {
        if (usuario.getRol() == RolUsuario.ADMINISTRADOR && rolNuevo != RolUsuario.ADMINISTRADOR
                && usuarioRepository.countByRol(RolUsuario.ADMINISTRADOR) <= 1) {
            throw new OperacionNoPermitidaException(
                    "No se puede " + (rolNuevo == null ? "eliminar" : "quitar el rol al")
                            + " último administrador del sistema");
        }
    }

    private void validarContrasena(String contrasena) {
        if (contrasena.length() < LONGITUD_MINIMA_CONTRASENA) {
            throw new DatoInvalidoException(
                    "La contraseña debe tener al menos " + LONGITUD_MINIMA_CONTRASENA + " caracteres");
        }
        if (contrasena.getBytes(StandardCharsets.UTF_8).length > LONGITUD_MAXIMA_CONTRASENA_BYTES) {
            throw new DatoInvalidoException(
                    "La contraseña admite hasta " + LONGITUD_MAXIMA_CONTRASENA_BYTES + " caracteres");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return usuarioRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exist(Long id) {
        return usuarioRepository.existsById(id);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    private void validarCampos(UsuarioDTO dto) {
        if (dto == null) {
            throw new DatoInvalidoException("Los datos del usuario son obligatorios");
        }
        if (dto.getNombreUsuario() == null || dto.getNombreUsuario().isBlank()) {
            throw new DatoInvalidoException("El nombre de usuario es obligatorio");
        }
        if (dto.getNombreUsuario().trim().length() > LONGITUD_MAXIMA_NOMBRE_USUARIO) {
            throw new DatoInvalidoException(
                    "El nombre de usuario admite hasta " + LONGITUD_MAXIMA_NOMBRE_USUARIO + " caracteres");
        }
        if (dto.getRol() == null) {
            throw new DatoInvalidoException("El rol del usuario es obligatorio");
        }
        if (dto.getRol() == RolUsuario.DOCENTE && dto.getDocenteId() == null) {
            throw new DatoInvalidoException("Un usuario con rol Docente debe estar vinculado a un docente");
        }
        if (dto.getRol() != RolUsuario.DOCENTE && dto.getDocenteId() != null) {
            throw new DatoInvalidoException("Solo un usuario con rol Docente puede vincularse a un docente");
        }
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            validarContrasena(dto.getContrasena());
        }
    }

    private void copiarCampos(UsuarioDTO dto, Usuario usuario) {
        usuario.setNombreUsuario(dto.getNombreUsuario().trim());
        usuario.setRol(dto.getRol());
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            usuario.setHashContrasena(passwordEncoder.encode(dto.getContrasena()));
        }
        Docente docente = null;
        if (dto.getDocenteId() != null) {
            docente = docenteRepository.findById(dto.getDocenteId()).orElseThrow(
                    () -> new RecursoNoEncontradoException("No existe el docente con id " + dto.getDocenteId()));
        }
        usuario.setDocente(docente);
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getNombreUsuario(), null, usuario.getRol(),
                usuario.getDocente() == null ? null : usuario.getDocente().getId());
    }
}
