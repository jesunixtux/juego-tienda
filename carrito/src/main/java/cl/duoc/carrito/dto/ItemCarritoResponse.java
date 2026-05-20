package cl.duoc.carrito.dto;

import java.time.LocalDateTime;

public record ItemCarritoResponse(
        Long id,
        Long usuarioId,
        Long videojuegoId,
        String nombreVideojuego,
        Integer cantidad,
        Integer precioUnitario,
        Integer subtotal,
        LocalDateTime fechaAgregado
) {
}
