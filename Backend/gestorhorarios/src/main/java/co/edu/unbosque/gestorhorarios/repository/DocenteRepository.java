package co.edu.unbosque.gestorhorarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.gestorhorarios.entity.Docente;

public interface DocenteRepository extends JpaRepository<Docente, Long> {

}
