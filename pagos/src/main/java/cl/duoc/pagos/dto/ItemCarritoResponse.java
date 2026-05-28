package cl.duoc.pagos.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({
        "nombreUsuario",
        "nombreVideojuego",
        "resena",
        "cantidad",
        "precioUnitario",
        "subtotal",
        "fechaAgregado",
        "id",
        "usuarioId",
        "videojuegoId"
})
public record ItemCarritoResponse(
        String nombreUsuario,
        String nombreVideojuego,
        ResenaCarritoResponse resena,
        Integer cantidad,
        Integer precioUnitario,
        Integer subtotal,
        LocalDateTime fechaAgregado,
        Long id,
        Long usuarioId,
        Long videojuegoId
) {
}
