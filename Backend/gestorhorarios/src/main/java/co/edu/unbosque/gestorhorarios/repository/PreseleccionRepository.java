package co.edu.unbosque.gestorhorarios.repository;

import co.edu.unbosque.gestorhorarios.entity.Preseleccion;
import co.edu.unbosque.gestorhorarios.entity.PreseleccionId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PreseleccionRepository extends JpaRepository<Preseleccion, PreseleccionId> {

    @Query("SELECT p.grupo.id FROM Preseleccion p WHERE p.usuario.id = :usuarioId AND p.grupo.horario.id = :horarioId")
    List<Long> findGrupoIdsDeUsuarioEnHorario(@Param("usuarioId") Long usuarioId, @Param("horarioId") Long horarioId);

    @Query("SELECT DISTINCT p.grupo.horario.id FROM Preseleccion p WHERE p.usuario.id = :usuarioId")
    List<Long> findHorarioIdsConPreseleccionDeUsuario(@Param("usuarioId") Long usuarioId);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM Preseleccion p WHERE p.usuario.id = :usuarioId AND p.grupo.id IN "
            + "(SELECT g.id FROM Grupo g WHERE g.horario.id = :horarioId)")
    int deleteDeUsuarioEnHorario(@Param("usuarioId") Long usuarioId, @Param("horarioId") Long horarioId);

    @Query("SELECT p.grupo.id, COUNT(p) FROM Preseleccion p WHERE p.grupo.horario.id = :horarioId GROUP BY p.grupo.id")
    List<Object[]> contarPorGrupoEnHorario(@Param("horarioId") Long horarioId);
}
