package cl.duoc.carrito.service;

import cl.duoc.carrito.client.ResenaClient;
import cl.duoc.carrito.client.UsuarioClient;
import cl.duoc.carrito.client.VideoJuegoClient;
import cl.duoc.carrito.dto.AgregarItemCarritoRequest;
import cl.duoc.carrito.dto.ItemCarritoResponse;
import cl.duoc.carrito.dto.ResenaResponse;
import cl.duoc.carrito.dto.ResumenCarritoResponse;
import cl.duoc.carrito.dto.UsuarioResponse;
import cl.duoc.carrito.dto.VideoJuegoResponse;
import cl.duoc.carrito.exception.ExternalServiceException;
import cl.duoc.carrito.model.ItemCarrito;
import cl.duoc.carrito.repository.ItemCarritoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTests {

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
    void agregarCreaItemNuevoConPrecioDelMicroservicioVideojuegos() {
        AgregarItemCarritoRequest request = new AgregarItemCarritoRequest(2L, 10L, 2);
        when(videoJuegoClient.buscarPorId(10L)).thenReturn(videojuego(10L, "Minecraft", 19990));
        when(itemCarritoRepository.findByUsuarioIdAndVideojuegoId(2L, 10L)).thenReturn(Optional.empty());
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenAnswer(invocation -> {
            ItemCarrito item = invocation.getArgument(0);
            item.antesDeGuardar();
            return item;
        });

        ItemCarrito creado = carritoService.agregar(request);

        assertThat(creado.getUsuarioId()).isEqualTo(2L);
        assertThat(creado.getVideojuegoId()).isEqualTo(10L);
        assertThat(creado.getCantidad()).isEqualTo(2);
        assertThat(creado.getPrecioUnitario()).isEqualTo(19990);
        assertThat(creado.getSubtotal()).isEqualTo(39980);
    }

    @Test
    void agregarRechazaVideojuegoSinPrecioValido() {
        when(videoJuegoClient.buscarPorId(10L)).thenReturn(videojuego(10L, "Juego sin precio", 0));

        assertThatThrownBy(() -> carritoService.agregar(new AgregarItemCarritoRequest(2L, 10L, 1)))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("precio valido");
    }

    @Test
    void obtenerResumenIncluyeNombreUsuarioResenaYTotal() {
        ItemCarrito item = item(1L, 2L, 10L, 2, 19990);
        when(itemCarritoRepository.findByUsuarioId(2L)).thenReturn(List.of(item));
        when(usuarioClient.buscarPorId(2L)).thenReturn(usuario());
        when(videoJuegoClient.buscarPorId(10L)).thenReturn(videojuego(10L, "Minecraft", 19990));
        when(resenaClient.listarPorUsuario(2L)).thenReturn(List.of(new ResenaResponse(
                5L,
                2L,
                "minecraft",
                "Muy entretenido",
                5,
                LocalDate.of(2026, 5, 20),
                "Jesus Emilio",
                "jesus@tiendajuegos.cl")));

        ResumenCarritoResponse resumen = carritoService.obtenerResumen(2L);
        ItemCarritoResponse response = resumen.items().getFirst();

        assertThat(resumen.nombreUsuario()).isEqualTo("Jesus Emilio");
        assertThat(resumen.total()).isEqualTo(39980);
        assertThat(response.nombreVideojuego()).isEqualTo("Minecraft");
        assertThat(response.resena().comentario()).isEqualTo("Muy entretenido");
    }

    @Test
    void vaciarPorUsuarioDelegaEnRepositorio() {
        carritoService.vaciarPorUsuario(2L);

        verify(itemCarritoRepository).deleteByUsuarioId(2L);
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

    private UsuarioResponse usuario() {
        return new UsuarioResponse(2L, "Jesus", "Emilio", "jesus@tiendajuegos.cl", null, null, "CLIENTE", true, null);
    }
}
