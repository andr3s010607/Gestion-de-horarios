package co.edu.unbosque.gestorhorarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.gestorhorarios.entity.Docente;

public interface DocenteRepository extends JpaRepository<Docente, Long> {

    Optional<Docente> findByCorreo(String correo);

    @Transactional
    void deleteByCorreo(String correo);
}
