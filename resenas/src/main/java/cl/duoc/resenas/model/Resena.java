package cl.duoc.resenas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "resenas")
@Schema(description = "Resena realizada por un usuario sobre un videojuego.")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID autogenerado de la resena.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "id_usuario", nullable = false)
    @Schema(description = "ID del usuario que realiza la resena.", example = "1")
    private Long usuarioId;

    @NotBlank(message = "El nombre del juego es obligatorio")
    @Size(max = 100)
    @Column(nullable = false)
    @Schema(description = "Nombre del juego resenado.", example = "God of War Ragnarok")
    private String nombreJuego;

    @NotBlank(message = "El comentario es obligatorio")
    @Column(nullable = false, length = 1000)
    @Schema(description = "Comentario de la resena.", example = "Excelente historia y combate muy fluido.")
    private String comentario;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    @Schema(description = "Puntuacion entre 1 y 5.", example = "5", minimum = "1", maximum = "5")
    private Integer puntuacion;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    @Schema(description = "Fecha de la resena.", example = "2026-06-22")
    private LocalDate fechaResena;
}
