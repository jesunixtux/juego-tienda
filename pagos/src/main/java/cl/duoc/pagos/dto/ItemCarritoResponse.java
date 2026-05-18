package cl.duoc.pagos.dto;

public record ItemCarritoResponse(
        Long id,
        Long usuarioId,
        Long videojuegoId,
        Integer cantidad,
        Integer precioUnitario,
        Integer subtotal
) {
}
