package co.edu.unbosque.gestorhorarios.repository;

import co.edu.unbosque.gestorhorarios.entity.Usuario;
import co.edu.unbosque.gestorhorarios.enums.RolUsuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);

    boolean existsByNombreUsuarioIgnoreCaseAndIdNot(String nombreUsuario, Long id);

    long countByRol(RolUsuario rol);

    List<Usuario> findByDocenteId(Long docenteId);
}
