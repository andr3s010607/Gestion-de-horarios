package co.edu.unbosque.gestorhorarios.repository;

import co.edu.unbosque.gestorhorarios.entity.Horario;
import co.edu.unbosque.gestorhorarios.enums.EstadoHorario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    boolean existsBySemestre(String semestre);

    List<Horario> findByEstado(EstadoHorario estado);

    boolean existsBySemestreAndIdNot(String semestre, Long id);
}
