package cl.duoc.pagos.dto;

import java.util.List;

public record ResumenCarritoResponse(
        Long usuarioId,
        List<ItemCarritoResponse> items,
        Integer total
) {
}
