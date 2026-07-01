package cl.duoc.pedidos.controller;

import cl.duoc.pedidos.dto.PedidoDTO;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.service.PedidoService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PedidoControllerUnitTests {

    private final PedidoService pedidoService = mock(PedidoService.class);
    private final PedidoController controller = new PedidoController(pedidoService);

    @Test
    void consultasCubrenListadoDetalleUsuarioFechaYPrecio() {
        PedidoDTO dto = dto();
        when(pedidoService.findAllConDetalle()).thenReturn(List.of(dto));
        when(pedidoService.findByIdConDetalle(1L)).thenReturn(Optional.of(dto));
        when(pedidoService.findByIdConDetalle(99L)).thenReturn(Optional.empty());
        when(pedidoService.findPedidosConUsuario()).thenReturn(List.of(dto));
        when(pedidoService.findByUsuarioConDetalle(2L)).thenReturn(List.of(dto));
        when(pedidoService.findByFechaPedidoBetweenConDetalle(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)))
                .thenReturn(List.of(dto));
        when(pedidoService.findByPrecioBetweenConDetalle(10000.0, 30000.0)).thenReturn(List.of(dto));

        assertThat(controller.listar().getBody()).containsExactly(dto);
        assertThat(controller.buscar(1L).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.buscar(99L).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.detalle().getBody()).containsExactly(dto);
        assertThat(controller.pedidosPorUsuario(2L).getBody()).containsExactly(dto);
        assertThat(controller.pedidosPorFecha(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)).getBody()).containsExactly(dto);
        assertThat(controller.pedidosPorPrecio(10000.0, 30000.0).getBody()).containsExactly(dto);
    }

    @Test
    void escrituraCubreCrearActualizarYBorrar() {
        Pedido pedido = pedido();
        PedidoDTO dto = dto();
        when(pedidoService.saveConDetalle(pedido)).thenReturn(dto);
        when(pedidoService.updateConDetalle(1L, pedido)).thenReturn(Optional.of(dto));
        when(pedidoService.updateConDetalle(99L, pedido)).thenReturn(Optional.empty());
        when(pedidoService.delete(1L)).thenReturn(true);
        when(pedidoService.delete(99L)).thenReturn(false);

        assertThat(controller.crear(pedido).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.actualizar(1L, pedido).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.actualizar(99L, pedido).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.borrar(1L).getStatusCode().value()).isEqualTo(204);
        assertThat(controller.borrar(99L).getStatusCode().value()).isEqualTo(404);
    }

    private Pedido pedido() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuarioId(2L);
        pedido.setNombreJuego("Minecraft");
        pedido.setPrecio(19990.0);
        pedido.setFechaPedido(LocalDate.of(2026, 5, 20));
        return pedido;
    }

    private PedidoDTO dto() {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(1L);
        dto.setUsuarioId(2L);
        dto.setNombreUsuario("Jesus Emilio");
        dto.setCorreoUsuario("jesus@tienda.cl");
        dto.setNombreJuego("Minecraft");
        dto.setPrecio(19990.0);
        dto.setFechaPedido(LocalDate.of(2026, 5, 20));
        return dto;
    }
}
