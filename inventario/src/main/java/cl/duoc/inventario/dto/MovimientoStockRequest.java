package cl.duoc.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para entrada o salida de stock.")
public record MovimientoStockRequest(
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        @Schema(description = "Cantidad que entra o sale del inventario.", example = "3", minimum = "1")
        Integer cantidad
) {
}
