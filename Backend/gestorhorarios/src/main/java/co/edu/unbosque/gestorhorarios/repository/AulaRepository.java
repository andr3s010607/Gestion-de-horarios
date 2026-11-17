package co.edu.unbosque.gestorhorarios.repository;

import co.edu.unbosque.gestorhorarios.entity.Aula;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AulaRepository extends JpaRepository<Aula, Long> {

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    List<Aula> findAllByOrderByCodigoAsc();
}
