package cl.duoc.resenas.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Resena enriquecida con datos del usuario.")
@JsonPropertyOrder({
        "nombreUsuario",
        "nombreJuego",
        "comentario",
        "puntuacion",
        "fechaResena",
        "id",
        "usuarioId",
        "correoUsuario"
})
public class ResenaDTO {
    @Schema(description = "Nombre del usuario.", example = "Jesus Emilio")
    private String nombreUsuario;
    @Schema(description = "Nombre del juego resenado.", example = "God of War Ragnarok")
    private String nombreJuego;
    @Schema(description = "Comentario de la resena.", example = "Excelente historia y combate muy fluido.")
    private String comentario;
    @Schema(description = "Puntuacion entre 1 y 5.", example = "5")
    private Integer puntuacion;
    @Schema(description = "Fecha de la resena.", example = "2026-06-22")
    private LocalDate fechaResena;
    @Schema(description = "ID de la resena.", example = "1")
    private Long id;
    @Schema(description = "ID del usuario.", example = "1")
    private Long usuarioId;
    @Schema(description = "Correo del usuario.", example = "jesus@duoc.cl")
    private String correoUsuario;
}
