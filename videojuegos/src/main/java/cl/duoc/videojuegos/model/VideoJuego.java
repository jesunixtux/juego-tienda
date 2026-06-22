package cl.duoc.videojuegos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video_juego")
@Schema(description = "Videojuego disponible en el catalogo de la tienda.")
public class VideoJuego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador autogenerado del videojuego.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre comercial del videojuego.", example = "The Legend of Zelda: Breath of the Wild")
    private String nombre;

    @NotBlank(message = "La categoria es obligatoria")
    @Schema(description = "Categoria o genero principal.", example = "Aventura")
    private String categoria;

    @NotNull
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio en pesos chilenos.", example = "49990")
    private Integer precio;

    @NotBlank(message = "La plataforma es obligatoria")
    @Schema(description = "Plataforma soportada por el juego.", example = "Nintendo Switch", allowableValues = {"PS4", "XBOX", "PC", "Nintendo Switch", "PS5"})
    private String plataforma;

    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    @Schema(description = "Descripcion breve del videojuego.", example = "Aventura de mundo abierto con exploracion, combate y puzzles.")
    private String descripcion;

    @Schema(description = "Estudio desarrollador.", example = "Nintendo")
    private String desarrollador;
    @Schema(description = "Fecha de lanzamiento en formato ISO.", example = "2017-03-03")
    private LocalDate fechaLanzamiento;
    @Schema(description = "URL de imagen referencial.", example = "https://example.com/zelda.jpg")
    private String imagenUrl;
    @Schema(description = "Indica si el videojuego esta disponible.", example = "true")
    private Boolean activo = true;

}
