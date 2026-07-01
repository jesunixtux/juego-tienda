package cl.duoc.carrito.controller;

import cl.duoc.carrito.dto.ActualizarCantidadRequest;
import cl.duoc.carrito.dto.AgregarItemCarritoRequest;
import cl.duoc.carrito.dto.ItemCarritoResponse;
import cl.duoc.carrito.dto.ResumenCarritoResponse;
import cl.duoc.carrito.service.CarritoService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CarritoControllerUnitTests {

    private final CarritoService carritoService = mock(CarritoService.class);
    private final CarritoController controller = new CarritoController(carritoService);

    @Test
    void endpointsDeConsultaRetornanItemsYResumen() {
        ItemCarritoResponse item = item();
        ResumenCarritoResponse resumen = new ResumenCarritoResponse("Jesus Emilio", List.of(item), 19990, 2L);
        when(carritoService.listarPorUsuarioConVideojuego(2L)).thenReturn(List.of(item));
        when(carritoService.obtenerResumen(2L)).thenReturn(resumen);
        when(carritoService.buscarPorIdConVideojuego(1L)).thenReturn(Optional.of(item));
        when(carritoService.buscarPorIdConVideojuego(99L)).thenReturn(Optional.empty());

        assertThat(controller.listarPorUsuario(2L).getBody()).containsExactly(item);
        assertThat(controller.obtenerResumen(2L).getBody()).isEqualTo(resumen);
        assertThat(controller.buscarPorId(1L).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.buscarPorId(99L).getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void endpointsDeEscrituraDeleganEnService() {
        ItemCarritoResponse item = item();
        AgregarItemCarritoRequest agregar = new AgregarItemCarritoRequest(2L, 10L, 1);
        ActualizarCantidadRequest actualizar = new ActualizarCantidadRequest(3);
        when(carritoService.agregarConVideojuego(agregar)).thenReturn(item);
        when(carritoService.actualizarCantidadConVideojuego(1L, actualizar)).thenReturn(Optional.of(item));
        when(carritoService.actualizarCantidadConVideojuego(99L, actualizar)).thenReturn(Optional.empty());
        when(carritoService.eliminar(1L)).thenReturn(true);
        when(carritoService.eliminar(99L)).thenReturn(false);

        assertThat(controller.agregar(agregar).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.actualizarCantidad(1L, actualizar).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.actualizarCantidad(99L, actualizar).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.eliminar(1L).getStatusCode().value()).isEqualTo(204);
        assertThat(controller.eliminar(99L).getStatusCode().value()).isEqualTo(404);

        assertThat(controller.vaciarPorUsuario(2L).getStatusCode().value()).isEqualTo(204);
        verify(carritoService).vaciarPorUsuario(2L);
    }

    private ItemCarritoResponse item() {
        return new ItemCarritoResponse("Jesus Emilio", "Minecraft", null, 1, 19990, 19990, null, 1L, 2L, 10L);
    }
}
