package cl.duoc.carrito.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Resena resumida asociada al juego del carrito.")
public record ResenaCarritoResponse(
        @Schema(description = "ID de la resena.", example = "4")
        Long id,
        @Schema(description = "Comentario del usuario.", example = "Excelente juego, muy recomendado.")
        String comentario,
        @Schema(description = "Puntuacion entre 1 y 5.", example = "5")
        Integer puntuacion,
        @Schema(description = "Fecha de la resena.", example = "2026-06-20")
        LocalDate fechaResena
) {
}
