package cl.duoc.inventario.dto;

import java.time.LocalDateTime;

public record InventarioResponse(
        Long id,
        Long videojuegoId,
        String nombreVideojuego,
        Integer stock,
        Integer stockMinimo,
        LocalDateTime fechaActualizacion
) {
}
