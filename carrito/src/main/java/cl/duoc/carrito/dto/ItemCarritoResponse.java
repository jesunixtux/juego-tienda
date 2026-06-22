package cl.duoc.carrito.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Item del carrito enriquecido con nombre de usuario, videojuego y resena del mismo usuario si existe.")
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
        @Schema(description = "Nombre del usuario dueno del carrito.", example = "Jesus Emilio")
        String nombreUsuario,
        @Schema(description = "Nombre del videojuego agregado.", example = "God of War Ragnarok")
        String nombreVideojuego,
        @Schema(description = "Resena realizada por el mismo usuario para ese juego, si existe.")
        ResenaCarritoResponse resena,
        @Schema(description = "Cantidad agregada.", example = "2")
        Integer cantidad,
        @Schema(description = "Precio unitario al momento de agregar.", example = "59990")
        Integer precioUnitario,
        @Schema(description = "Subtotal calculado cantidad * precioUnitario.", example = "119980")
        Integer subtotal,
        @Schema(description = "Fecha en que se agrego el item.", example = "2026-06-22T16:30:00")
        LocalDateTime fechaAgregado,
        @Schema(description = "ID del item del carrito.", example = "1")
        Long id,
        @Schema(description = "ID del usuario.", example = "1")
        Long usuarioId,
        @Schema(description = "ID del videojuego.", example = "2")
        Long videojuegoId
) {
}
