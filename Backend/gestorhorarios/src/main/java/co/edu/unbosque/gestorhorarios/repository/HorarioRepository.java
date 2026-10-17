package co.edu.unbosque.gestorhorarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.gestorhorarios.entity.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

}
