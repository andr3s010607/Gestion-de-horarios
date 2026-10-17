package co.edu.unbosque.gestorhorarios.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.gestorhorarios.dto.UsuarioDTO;
import co.edu.unbosque.gestorhorarios.entity.Usuario;
import co.edu.unbosque.gestorhorarios.repository.UsuarioRepository;
import co.edu.unbosque.gestorhorarios.enums.RolUsuario;

@Service
public class UsuarioService implements CRUDOperation<UsuarioDTO> {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public int create(UsuarioDTO dto) {
        try {
            if (dto == null) {
                return 1;
            }
            if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent()) {
                return 1;
            }
            Usuario entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            usuarioRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public List<UsuarioDTO> getAll() {
        return usuarioRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public UsuarioDTO getById(Long id) {
        return usuarioRepository.findById(id).map(this::convertirADTO).orElse(null);
    }

    @Override
    public int deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            return 1;
        }
        usuarioRepository.deleteById(id);
        return 0;
    }

    @Override
    public int updateById(Long id, UsuarioDTO dto) {
        try {
            if (!usuarioRepository.existsById(id) || dto == null) {
                return 1;
            }
            if (usuarioRepository.findByCorreo(dto.getCorreo())
                    .filter(usuario -> !usuario.getId().equals(id)).isPresent()) {
                return 1;
            }
            Usuario actual = usuarioRepository.findById(id).orElse(null);
            Usuario entity = convertirAEntidad(dto);
            if (entity == null) {
                return 1;
            }
            if (dto.getContrasena() == null || dto.getContrasena().isBlank()) {
                entity.setContrasena(actual.getContrasena());
            }
            entity.setId(id);
            usuarioRepository.save(entity);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public long count() {
        return usuarioRepository.count();
    }

    @Override
    public boolean exist(Long id) {
        return usuarioRepository.existsById(id);
    }

    private Usuario convertirAEntidad(UsuarioDTO dto) {
        Usuario entity = new Usuario();
        entity.setNombreCompleto(dto.getNombreCompleto());
        entity.setCorreo(dto.getCorreo());
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            entity.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }
        entity.setRol(dto.getRol());
        entity.setActivo(dto.isActivo());
        return entity;
    }

    private UsuarioDTO convertirADTO(Usuario entity) {
        return new UsuarioDTO(entity.getId(), entity.getNombreCompleto(), entity.getCorreo(), null, entity.getRol(), entity.isActivo());
    }
}

