package cl.duoc.pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud para cambiar el estado de un pago.")
public record ActualizarEstadoPagoRequest(
        @NotBlank(message = "El estado es obligatorio")
        @Schema(description = "Nuevo estado de la transaccion.", example = "APROBADO", allowableValues = {"APROBADO", "PENDIENTE", "RECHAZADO", "ANULADO"})
        String estado
) {
}
