package cl.duoc.carrito.repository;

import cl.duoc.carrito.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByUsuarioId(Long usuarioId);

    Optional<ItemCarrito> findByUsuarioIdAndVideojuegoId(Long usuarioId, Long videojuegoId);

    void deleteByUsuarioId(Long usuarioId);
}
