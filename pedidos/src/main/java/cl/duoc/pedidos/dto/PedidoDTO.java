package cl.duoc.pedidos.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PedidoDTO {
    private Long usuarioId;
    private String nombreJuego;
    private Double precio;
    private LocalDate fechaPedido;
    private String nombreUsuario;
    private String correoUsuario;
}
