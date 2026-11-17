package co.edu.unbosque.gestorhorarios.repository;

import co.edu.unbosque.gestorhorarios.entity.SesionClase;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionClaseRepository extends JpaRepository<SesionClase, Long> {

    List<SesionClase> findByHorarioId(Long horarioId);

    List<SesionClase> findByHorarioIdAndDiaIsNotNull(Long horarioId);

    List<SesionClase> findByHorarioIdAndDiaIsNull(Long horarioId);

    List<SesionClase> findByHorarioIdAndGrupoDocenteIdAndDiaIsNotNull(Long horarioId, Long docenteId);

    List<SesionClase> findByHorarioIdAndAulaIdAndDiaIsNotNull(Long horarioId, Long aulaId);

    List<SesionClase> findByHorarioIdAndGrupoAsignaturaIdAndDiaIsNotNull(Long horarioId, Long asignaturaId);

    boolean existsByAulaId(Long aulaId);
}
