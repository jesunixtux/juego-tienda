package cl.duoc.pedidos.repository;

import cl.duoc.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioId(Long usuarioId);

    List<Pedido> findByFechaPedidoBetween(LocalDate desde, LocalDate hasta);

    List<Pedido> findByPrecioBetween(Double minimo, Double maximo);
}
