package cl.duoc.carrito.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AgregarItemCarritoRequest(
        @NotNull(message = "El usuario es obligatorio")
        Long usuarioId,

        @NotNull(message = "El videojuego es obligatorio")
        Long videojuegoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer cantidad
) {
}
