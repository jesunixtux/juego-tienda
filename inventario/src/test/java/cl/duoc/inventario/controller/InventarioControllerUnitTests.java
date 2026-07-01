package cl.duoc.inventario.controller;

import cl.duoc.inventario.dto.ActualizarStockRequest;
import cl.duoc.inventario.dto.CrearInventarioRequest;
import cl.duoc.inventario.dto.InventarioResponse;
import cl.duoc.inventario.dto.MovimientoStockRequest;
import cl.duoc.inventario.service.InventarioService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InventarioControllerUnitTests {

    private final InventarioService inventarioService = mock(InventarioService.class);
    private final InventarioController controller = new InventarioController(inventarioService);

    @Test
    void consultasCubrenListadoBusquedaYBajoStock() {
        InventarioResponse inventario = inventario();
        when(inventarioService.listarConVideojuego()).thenReturn(List.of(inventario));
        when(inventarioService.listarBajoStockConVideojuego()).thenReturn(List.of(inventario));
        when(inventarioService.buscarPorIdConVideojuego(1L)).thenReturn(Optional.of(inventario));
        when(inventarioService.buscarPorIdConVideojuego(99L)).thenReturn(Optional.empty());
        when(inventarioService.buscarPorVideojuegoConDetalle(10L)).thenReturn(Optional.of(inventario));
        when(inventarioService.buscarPorVideojuegoConDetalle(99L)).thenReturn(Optional.empty());

        assertThat(controller.listar().getBody()).containsExactly(inventario);
        assertThat(controller.listarBajoStock().getBody()).containsExactly(inventario);
        assertThat(controller.buscarPorId(1L).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.buscarPorId(99L).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.buscarPorVideojuego(10L).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.buscarPorVideojuego(99L).getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void escrituraCubrenCrearActualizarMovimientosYEliminar() {
        InventarioResponse inventario = inventario();
        CrearInventarioRequest crear = new CrearInventarioRequest(10L, 8, 2);
        ActualizarStockRequest stock = new ActualizarStockRequest(12);
        MovimientoStockRequest movimiento = new MovimientoStockRequest(3);
        when(inventarioService.crearConDetalle(crear)).thenReturn(inventario);
        when(inventarioService.actualizarConDetalle(1L, crear)).thenReturn(Optional.of(inventario));
        when(inventarioService.actualizarConDetalle(99L, crear)).thenReturn(Optional.empty());
        when(inventarioService.actualizarStockPorVideojuegoConDetalle(10L, stock)).thenReturn(Optional.of(inventario));
        when(inventarioService.actualizarStockPorVideojuegoConDetalle(99L, stock)).thenReturn(Optional.empty());
        when(inventarioService.aumentarStockConDetalle(10L, movimiento)).thenReturn(Optional.of(inventario));
        when(inventarioService.aumentarStockConDetalle(99L, movimiento)).thenReturn(Optional.empty());
        when(inventarioService.disminuirStockConDetalle(10L, movimiento)).thenReturn(Optional.of(inventario));
        when(inventarioService.disminuirStockConDetalle(99L, movimiento)).thenReturn(Optional.empty());
        when(inventarioService.eliminar(1L)).thenReturn(true);
        when(inventarioService.eliminar(99L)).thenReturn(false);

        assertThat(controller.crear(crear).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.actualizar(1L, crear).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.actualizar(99L, crear).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.actualizarStockPorVideojuego(10L, stock).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.actualizarStockPorVideojuego(99L, stock).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.aumentarStock(10L, movimiento).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.aumentarStock(99L, movimiento).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.disminuirStock(10L, movimiento).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.disminuirStock(99L, movimiento).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.eliminar(1L).getStatusCode().value()).isEqualTo(204);
        assertThat(controller.eliminar(99L).getStatusCode().value()).isEqualTo(404);
    }

    private InventarioResponse inventario() {
        return new InventarioResponse("Minecraft", 8, 2, null, 1L, 10L);
    }
}
