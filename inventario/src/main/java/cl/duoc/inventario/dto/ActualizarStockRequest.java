package cl.duoc.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para fijar el stock exacto de un videojuego.")
public record ActualizarStockRequest(
        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        @Schema(description = "Nuevo stock exacto.", example = "20", minimum = "0")
        Integer stock
) {
}
