package cl.duoc.pedidos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pedidos")
@Schema(description = "Pedido realizado por un usuario.")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID autogenerado del pedido.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "id_usuario", nullable = false)
    @Schema(description = "ID del usuario que realizo el pedido.", example = "1")
    private Long usuarioId;

    @NotBlank(message = "El nombre del juego es obligatorio")
    @Size(max = 100)
    @Column(nullable = false)
    @Schema(description = "Nombre del juego comprado.", example = "God of War Ragnarok")
    private String nombreJuego;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    @Schema(description = "Precio del pedido.", example = "59990")
    private Double precio;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    @Schema(description = "Fecha del pedido en formato ISO.", example = "2026-06-22")
    private LocalDate fechaPedido;
}
