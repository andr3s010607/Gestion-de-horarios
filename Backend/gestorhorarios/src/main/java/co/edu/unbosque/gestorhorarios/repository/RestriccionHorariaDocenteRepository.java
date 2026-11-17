package co.edu.unbosque.gestorhorarios.repository;

import co.edu.unbosque.gestorhorarios.entity.RestriccionHorariaDocente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestriccionHorariaDocenteRepository extends JpaRepository<RestriccionHorariaDocente, Long> {

    List<RestriccionHorariaDocente> findByDocenteId(Long docenteId);

    void deleteByDocenteId(Long docenteId);
}
