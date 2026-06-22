package cl.duoc.carrito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para agregar un videojuego al carrito de un usuario.")
public record AgregarItemCarritoRequest(
        @NotNull(message = "El usuario es obligatorio")
        @Schema(description = "ID del usuario comprador.", example = "1")
        Long usuarioId,

        @NotNull(message = "El videojuego es obligatorio")
        @Schema(description = "ID del videojuego a agregar.", example = "2")
        Long videojuegoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        @Schema(description = "Cantidad de unidades.", example = "1", minimum = "1")
        Integer cantidad
) {
}
