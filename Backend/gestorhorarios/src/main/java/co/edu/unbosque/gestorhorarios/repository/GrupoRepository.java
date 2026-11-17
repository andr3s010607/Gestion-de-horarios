package co.edu.unbosque.gestorhorarios.repository;

import co.edu.unbosque.gestorhorarios.entity.Grupo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    boolean existsByAsignaturaId(Long asignaturaId);

    boolean existsByDocenteId(Long docenteId);

    boolean existsByHorarioIdAndAsignaturaIdAndNumero(Long horarioId, Long asignaturaId, Integer numero);

    boolean existsByHorarioIdAndAsignaturaIdAndNumeroAndIdNot(Long horarioId, Long asignaturaId, Integer numero,
            Long id);

    List<Grupo> findByHorarioId(Long horarioId);
}
