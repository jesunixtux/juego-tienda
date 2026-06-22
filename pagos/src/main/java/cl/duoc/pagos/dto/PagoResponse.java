package cl.duoc.pagos.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Pago registrado con datos enriquecidos del usuario.")
@JsonPropertyOrder({
        "nombreUsuario",
        "monto",
        "metodoPago",
        "estado",
        "codigoTransaccion",
        "fechaPago",
        "id",
        "usuarioId"
})
public record PagoResponse(
        @Schema(description = "Nombre del usuario que realizo el pago.", example = "Jesus Emilio")
        String nombreUsuario,
        @Schema(description = "Monto total pagado.", example = "119980")
        Integer monto,
        @Schema(description = "Metodo de pago.", example = "TARJETA")
        String metodoPago,
        @Schema(description = "Estado actual de la transaccion.", example = "APROBADO")
        String estado,
        @Schema(description = "Codigo unico de transaccion.", example = "PAY-20260622-0001")
        String codigoTransaccion,
        @Schema(description = "Fecha de pago.", example = "2026-06-22T16:30:00")
        LocalDateTime fechaPago,
        @Schema(description = "ID del pago.", example = "1")
        Long id,
        @Schema(description = "ID del usuario.", example = "1")
        Long usuarioId
) {
}
