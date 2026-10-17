package co.edu.unbosque.gestorhorarios.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.gestorhorarios.entity.SesionClase;

public interface SesionClaseRepository extends JpaRepository<SesionClase, Long> {

    List<SesionClase> findByHorarioId(Long horarioId);
}
