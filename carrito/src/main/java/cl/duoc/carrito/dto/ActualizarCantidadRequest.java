package cl.duoc.carrito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para modificar la cantidad de un item del carrito.")
public record ActualizarCantidadRequest(
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        @Schema(description = "Nueva cantidad del item.", example = "3", minimum = "1")
        Integer cantidad
) {
}
