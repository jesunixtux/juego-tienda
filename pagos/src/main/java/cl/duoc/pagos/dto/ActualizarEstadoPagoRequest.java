package cl.duoc.pagos.dto;

import jakarta.validation.constraints.NotBlank;

public record ActualizarEstadoPagoRequest(
        @NotBlank(message = "El estado es obligatorio")
        String estado
) {
}
