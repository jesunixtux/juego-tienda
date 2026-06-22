package cl.duoc.inventario.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta de inventario enriquecida con nombre del videojuego.")
@JsonPropertyOrder({"nombreVideojuego", "stock", "stockMinimo", "fechaActualizacion", "id", "videojuegoId"})
public record InventarioResponse(
        @Schema(description = "Nombre del videojuego.", example = "God of War Ragnarok")
        String nombreVideojuego,
        @Schema(description = "Stock actual.", example = "15")
        Integer stock,
        @Schema(description = "Stock minimo configurado.", example = "5")
        Integer stockMinimo,
        @Schema(description = "Fecha de ultima actualizacion.", example = "2026-06-22T16:30:00")
        LocalDateTime fechaActualizacion,
        @Schema(description = "ID del registro de inventario.", example = "1")
        Long id,
        @Schema(description = "ID del videojuego.", example = "2")
        Long videojuegoId
) {
}
