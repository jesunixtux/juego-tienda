package cl.duoc.inventario.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({"nombreVideojuego", "stock", "stockMinimo", "fechaActualizacion", "id", "videojuegoId"})
public record InventarioResponse(
        String nombreVideojuego,
        Integer stock,
        Integer stockMinimo,
        LocalDateTime fechaActualizacion,
        Long id,
        Long videojuegoId
) {
}
