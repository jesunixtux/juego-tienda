package cl.duoc.resenas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "id_usuario", nullable = false)
    private Long usuarioId;

    @NotBlank(message = "El nombre del juego es obligatorio")
    @Size(max = 100)
    @Column(nullable = false)
    private String nombreJuego;

    @NotBlank(message = "El comentario es obligatorio")
    @Column(nullable = false, length = 1000)
    private String comentario;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer puntuacion;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaResena;
}
