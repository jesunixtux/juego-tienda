package cl.duoc.usuarios.repository;

import cl.duoc.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    List<Usuario> findByActivoTrue();

    boolean existsByCorreoIgnoreCase(String correo);
}
