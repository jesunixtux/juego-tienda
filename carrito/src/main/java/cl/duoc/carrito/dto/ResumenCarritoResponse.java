package cl.duoc.carrito.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resumen monetario del carrito de un usuario.")
@JsonPropertyOrder({"nombreUsuario", "items", "total", "usuarioId"})
public record ResumenCarritoResponse(
        @Schema(description = "Nombre del usuario.", example = "Jesus Emilio")
        String nombreUsuario,
        @Schema(description = "Items actuales del carrito.")
        List<ItemCarritoResponse> items,
        @Schema(description = "Total del carrito.", example = "119980")
        Integer total,
        @Schema(description = "ID del usuario.", example = "1")
        Long usuarioId
) {
}
