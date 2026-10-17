package co.edu.unbosque.gestorhorarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.gestorhorarios.entity.ParametroSistema;

public interface ParametroSistemaRepository extends JpaRepository<ParametroSistema, Long> {

    Optional<ParametroSistema> findByClave(String clave);
}
