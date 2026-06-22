package cl.duoc.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para crear o actualizar un registro de inventario.")
public record CrearInventarioRequest(
        @NotNull(message = "El videojuego es obligatorio")
        @Schema(description = "ID del videojuego asociado al stock.", example = "2")
        Long videojuegoId,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        @Schema(description = "Stock actual disponible.", example = "15", minimum = "0")
        Integer stock,

        @NotNull(message = "El stock minimo es obligatorio")
        @Min(value = 0, message = "El stock minimo no puede ser negativo")
        @Schema(description = "Umbral para considerar bajo stock.", example = "5", minimum = "0")
        Integer stockMinimo
) {
}
