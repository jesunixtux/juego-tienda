package cl.duoc.resenas.repository;

import cl.duoc.resenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByUsuarioId(Long usuarioId);

    List<Resena> findByPuntuacionBetween(Integer min, Integer max);

    List<Resena> findByFechaResenaBetween(LocalDate desde, LocalDate hasta);
}
