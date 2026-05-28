package cl.duoc.pedidos.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonPropertyOrder({"nombreUsuario", "nombreJuego", "precio", "fechaPedido", "id", "usuarioId", "correoUsuario"})
public class PedidoDTO {
    private String nombreUsuario;
    private String nombreJuego;
    private Double precio;
    private LocalDate fechaPedido;
    private Long id;
    private Long usuarioId;
    private String correoUsuario;
}
