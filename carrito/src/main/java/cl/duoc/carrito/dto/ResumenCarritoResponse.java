package cl.duoc.carrito.dto;

import cl.duoc.carrito.model.ItemCarrito;

import java.util.List;

public record ResumenCarritoResponse(
        Long usuarioId,
        List<ItemCarrito> items,
        Integer total
) {
}
