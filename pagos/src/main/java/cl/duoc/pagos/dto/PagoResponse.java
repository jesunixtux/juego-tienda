package cl.duoc.pagos.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

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
        String nombreUsuario,
        Integer monto,
        String metodoPago,
        String estado,
        String codigoTransaccion,
        LocalDateTime fechaPago,
        Long id,
        Long usuarioId
) {
}
