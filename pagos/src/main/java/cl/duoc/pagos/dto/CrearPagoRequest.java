package cl.duoc.pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para crear un pago desde el resumen actual del carrito.")
public record CrearPagoRequest(
        @NotNull(message = "El usuario es obligatorio")
        @Schema(description = "ID del usuario que paga.", example = "1")
        Long usuarioId,

        @NotBlank(message = "El metodo de pago es obligatorio")
        @Schema(description = "Metodo de pago utilizado.", example = "TARJETA", allowableValues = {"TARJETA", "DEBITO", "TRANSFERENCIA", "PAYPAL"})
        String metodoPago
) {
}
