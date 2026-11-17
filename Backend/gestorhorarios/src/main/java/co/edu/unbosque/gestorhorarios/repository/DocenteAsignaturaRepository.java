package co.edu.unbosque.gestorhorarios.repository;

import co.edu.unbosque.gestorhorarios.entity.DocenteAsignatura;
import co.edu.unbosque.gestorhorarios.entity.DocenteAsignaturaId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteAsignaturaRepository extends JpaRepository<DocenteAsignatura, DocenteAsignaturaId> {

    List<DocenteAsignatura> findByDocenteId(Long docenteId);

    void deleteByDocenteId(Long docenteId);

    void deleteByAsignaturaId(Long asignaturaId);
}
