package cl.duoc.pagos.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"nombreUsuario", "items", "total", "usuarioId"})
public record ResumenCarritoResponse(
        String nombreUsuario,
        List<ItemCarritoResponse> items,
        Integer total,
        Long usuarioId
) {
}
