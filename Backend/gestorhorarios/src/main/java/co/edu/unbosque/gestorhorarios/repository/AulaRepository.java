package co.edu.unbosque.gestorhorarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.gestorhorarios.entity.Aula;

public interface AulaRepository extends JpaRepository<Aula, Long> {

    Optional<Aula> findByCodigo(String codigo);

    @Transactional
    void deleteByCodigo(String codigo);
}
