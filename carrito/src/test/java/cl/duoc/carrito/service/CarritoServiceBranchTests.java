package cl.duoc.carrito.service;

import cl.duoc.carrito.client.ResenaClient;
import cl.duoc.carrito.client.UsuarioClient;
import cl.duoc.carrito.client.VideoJuegoClient;
import cl.duoc.carrito.dto.ActualizarCantidadRequest;
import cl.duoc.carrito.dto.AgregarItemCarritoRequest;
import cl.duoc.carrito.dto.ItemCarritoResponse;
import cl.duoc.carrito.dto.ResumenCarritoResponse;
import cl.duoc.carrito.dto.UsuarioResponse;
import cl.duoc.carrito.dto.VideoJuegoResponse;
import cl.duoc.carrito.exception.ResourceNotFoundException;
import cl.duoc.carrito.model.ItemCarrito;
import cl.duoc.carrito.repository.ItemCarritoRepository;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoServiceBranchTests {

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private VideoJuegoClient videoJuegoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private ResenaClient resenaClient;

    @InjectMocks
    private CarritoService carritoService;

    @Test
    void agregarSumaCantidadSiItemYaExiste() {
        ItemCarrito existente = item(1L, 2L, 10L, 2, 10000);
        when(videoJuegoClient.buscarPorId(10L)).thenReturn(videojuego(10L, "Minecraft", 12000));
        when(itemCarritoRepository.findByUsuarioIdAndVideojuegoId(2L, 10L)).thenReturn(Optional.of(existente));
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenAnswer(invocation -> {
            ItemCarrito item = invocation.getArgument(0);
            item.antesDeActualizar();
            return item;
        });

        ItemCarrito resultado = carritoService.agregar(new AgregarItemCarritoRequest(2L, 10L, 3));

        assertThat(resultado.getCantidad()).isEqualTo(5);
        assertThat(resultado.getPrecioUnitario()).isEqualTo(12000);
        assertThat(resultado.getSubtotal()).isEqualTo(60000);
    }

    @Test
    void agregarTraduceVideojuegoNoEncontrado() {
        when(videoJuegoClient.buscarPorId(404L)).thenThrow(feignStatus(404));

        assertThatThrownBy(() -> carritoService.agregar(new AgregarItemCarritoRequest(2L, 404L, 1)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Videojuego no encontrado");
    }

    @Test
    void actualizarCantidadRetornaItemActualizadoOVacio() {
        ItemCarrito item = item(1L, 2L, 10L, 1, 15000);
        when(itemCarritoRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemCarritoRepository.findById(99L)).thenReturn(Optional.empty());
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<ItemCarrito> actualizado = carritoService.actualizarCantidad(1L, new ActualizarCantidadRequest(4));

        assertThat(actualizado).isPresent();
        assertThat(actualizado.get().getCantidad()).isEqualTo(4);
        assertThat(carritoService.actualizarCantidad(99L, new ActualizarCantidadRequest(2))).isEmpty();
    }

    @Test
    void buscarPorIdConVideojuegoUsaFallbacksCuandoServiciosRemotosFallan() {
        ItemCarrito item = item(1L, 2L, 10L, 1, 15000);
        when(itemCarritoRepository.findById(1L)).thenReturn(Optional.of(item));
        when(usuarioClient.buscarPorId(2L)).thenThrow(feignStatus(404));
        when(resenaClient.listarPorUsuario(2L)).thenThrow(feignStatus(500));
        when(videoJuegoClient.buscarPorId(10L)).thenThrow(feignStatus(500));

        Optional<ItemCarritoResponse> response = carritoService.buscarPorIdConVideojuego(1L);

        assertThat(response).isPresent();
        assertThat(response.get().nombreUsuario()).isEqualTo("Usuario no encontrado");
        assertThat(response.get().nombreVideojuego()).isEqualTo("Videojuego no disponible");
        assertThat(response.get().resena()).isNull();
    }

    @Test
    void obtenerResumenUsaCorreoComoNombreYResenasVaciasSiClienteRetornaNull() {
        ItemCarrito item = item(1L, 2L, 10L, 2, 15000);
        when(itemCarritoRepository.findByUsuarioId(2L)).thenReturn(List.of(item));
        when(usuarioClient.buscarPorId(2L)).thenReturn(new UsuarioResponse(
                2L,
                " ",
                null,
                "cliente@tiendajuegos.cl",
                null,
                null,
                "CLIENTE",
                true,
                null));
        when(resenaClient.listarPorUsuario(2L)).thenReturn(null);
        when(videoJuegoClient.buscarPorId(10L)).thenReturn(videojuego(10L, "Minecraft", 15000));

        ResumenCarritoResponse resumen = carritoService.obtenerResumen(2L);

        assertThat(resumen.nombreUsuario()).isEqualTo("cliente@tiendajuegos.cl");
        assertThat(resumen.total()).isEqualTo(30000);
        assertThat(resumen.items().getFirst().resena()).isNull();
    }

    @Test
    void eliminarRetornaTrueSoloCuandoExiste() {
        when(itemCarritoRepository.existsById(1L)).thenReturn(true);
        when(itemCarritoRepository.existsById(99L)).thenReturn(false);

        assertThat(carritoService.eliminar(1L)).isTrue();
        assertThat(carritoService.eliminar(99L)).isFalse();
        verify(itemCarritoRepository).deleteById(1L);
        verify(itemCarritoRepository, never()).deleteById(99L);
    }

    @Test
    void itemCarritoLifecycleCalculaSubtotal() {
        ItemCarrito item = item(1L, 2L, 10L, 2, 15000);

        item.antesDeGuardar();
        item.setCantidad(3);
        item.antesDeActualizar();

        assertThat(item.getFechaAgregado()).isNotNull();
        assertThat(item.getSubtotal()).isEqualTo(45000);
    }

    private ItemCarrito item(Long id, Long usuarioId, Long videojuegoId, Integer cantidad, Integer precio) {
        ItemCarrito item = new ItemCarrito();
        item.setId(id);
        item.setUsuarioId(usuarioId);
        item.setVideojuegoId(videojuegoId);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(precio);
        item.setSubtotal(cantidad * precio);
        return item;
    }

    private VideoJuegoResponse videojuego(Long id, String nombre, Integer precio) {
        return new VideoJuegoResponse(id, nombre, "Sandbox", precio, "PC", null, "Mojang", null, null, true);
    }

    private FeignException feignStatus(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/test",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null);
        Response response = Response.builder()
                .status(status)
                .reason("test")
                .request(request)
                .headers(Map.of())
                .build();
        return FeignException.errorStatus("test", response);
    }
}
