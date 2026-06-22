package cl.duoc.pedidos.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Pedido enriquecido con datos del usuario.")
@JsonPropertyOrder({"nombreUsuario", "nombreJuego", "precio", "fechaPedido", "id", "usuarioId", "correoUsuario"})
public class PedidoDTO {
    @Schema(description = "Nombre del usuario.", example = "Jesus Emilio")
    private String nombreUsuario;
    @Schema(description = "Nombre del juego comprado.", example = "God of War Ragnarok")
    private String nombreJuego;
    @Schema(description = "Precio del pedido.", example = "59990")
    private Double precio;
    @Schema(description = "Fecha del pedido.", example = "2026-06-22")
    private LocalDate fechaPedido;
    @Schema(description = "ID del pedido.", example = "1")
    private Long id;
    @Schema(description = "ID del usuario.", example = "1")
    private Long usuarioId;
    @Schema(description = "Correo del usuario.", example = "jesus@duoc.cl")
    private String correoUsuario;
}
