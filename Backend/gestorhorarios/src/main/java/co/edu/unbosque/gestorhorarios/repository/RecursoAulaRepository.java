package co.edu.unbosque.gestorhorarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.gestorhorarios.entity.RecursoAula;

public interface RecursoAulaRepository extends JpaRepository<RecursoAula, Long> {

    Optional<RecursoAula> findByNombre(String nombre);

    @Transactional
    void deleteByNombre(String nombre);
}
