package cl.duoc.inventario.repository;

import cl.duoc.inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByVideojuegoId(Long videojuegoId);

    boolean existsByVideojuegoId(Long videojuegoId);

    List<Inventario> findByStockLessThanEqual(Integer stock);
}
