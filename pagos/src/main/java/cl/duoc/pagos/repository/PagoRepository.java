package cl.duoc.pagos.repository;

import cl.duoc.pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByUsuarioId(Long usuarioId);

    boolean existsByCodigoTransaccion(String codigoTransaccion);
}
