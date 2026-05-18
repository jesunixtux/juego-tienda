package cl.duoc.authentication.repository;

import cl.duoc.authentication.model.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, Long> {

    Optional<Credencial> findByCorreoIgnoreCase(String correo);

    Optional<Credencial> findByUsuarioId(Long usuarioId);

    boolean existsByCorreoIgnoreCase(String correo);
}
