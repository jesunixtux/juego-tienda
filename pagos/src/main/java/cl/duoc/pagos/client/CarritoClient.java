package cl.duoc.pagos.client;

import cl.duoc.pagos.dto.ResumenCarritoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "carrito")
public interface CarritoClient {

    @GetMapping("/carrito/usuario/{usuarioId}/resumen")
    ResumenCarritoResponse obtenerResumen(@PathVariable("usuarioId") Long usuarioId);

    @DeleteMapping("/carrito/usuario/{usuarioId}")
    void vaciarPorUsuario(@PathVariable("usuarioId") Long usuarioId);
}
