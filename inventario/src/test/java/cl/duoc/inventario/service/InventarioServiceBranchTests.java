package cl.duoc.inventario.service;

import cl.duoc.inventario.client.VideoJuegoClient;
import cl.duoc.inventario.dto.ActualizarStockRequest;
import cl.duoc.inventario.dto.CrearInventarioRequest;
import cl.duoc.inventario.dto.InventarioResponse;
import cl.duoc.inventario.dto.MovimientoStockRequest;
import cl.duoc.inventario.dto.VideoJuegoResponse;
import cl.duoc.inventario.exception.ConflictException;
import cl.duoc.inventario.exception.ExternalServiceException;
import cl.duoc.inventario.exception.ResourceNotFoundException;
import cl.duoc.inventario.model.Inventario;
import cl.duoc.inventario.repository.InventarioRepository;
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
class InventarioServiceBranchTests {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private VideoJuegoClient videoJuegoClient;

    @InjectMocks
    private InventarioService inventarioService;

    @Test
    void listarYBuscarConDetalleEnriquecenVideojuego() {
        Inventario inventario = inventario(1L, 10L, 5, 2);
        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.findByVideojuegoId(10L)).thenReturn(Optional.of(inventario));
        when(videoJuegoClient.buscarPorId(10L)).thenReturn(videojuego());

        List<InventarioResponse> todos = inventarioService.listarConVideojuego();
        Optional<InventarioResponse> porId = inventarioService.buscarPorIdConVideojuego(1L);
        Optional<InventarioResponse> porVideojuego = inventarioService.buscarPorVideojuegoConDetalle(10L);

        assertThat(todos.getFirst().nombreVideojuego()).isEqualTo("Minecraft");
        assertThat(porId).isPresent();
        assertThat(porId.get().stock()).isEqualTo(5);
        assertThat(porVideojuego).isPresent();
        assertThat(porVideojuego.get().videojuegoId()).isEqualTo(10L);
    }

    @Test
    void crearConDetalleTraduceVideojuegoNoEncontradoYServicioCaido() {
        CrearInventarioRequest request404 = new CrearInventarioRequest(404L, 5, 2);
        CrearInventarioRequest request500 = new CrearInventarioRequest(500L, 5, 2);
        when(videoJuegoClient.buscarPorId(404L)).thenThrow(feignStatus(404));
        when(videoJuegoClient.buscarPorId(500L)).thenThrow(feignStatus(500));

        assertThatThrownBy(() -> inventarioService.crearConDetalle(request404))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Videojuego no encontrado");
        assertThatThrownBy(() -> inventarioService.crearConDetalle(request500))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("microservicio videojuegos");
    }

    @Test
    void actualizarModificaCamposOVuelveVacio() {
        Inventario existente = inventario(1L, 10L, 5, 2);
        CrearInventarioRequest request = new CrearInventarioRequest(11L, 20, 4);
        when(videoJuegoClient.buscarPorId(11L)).thenReturn(videojuego(11L, "Zelda"));
        when(inventarioRepository.findByVideojuegoId(11L)).thenReturn(Optional.empty());
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Inventario> actualizado = inventarioService.actualizar(1L, request);
        Optional<Inventario> vacio = inventarioService.actualizar(99L, request);

        assertThat(actualizado).isPresent();
        assertThat(actualizado.get().getVideojuegoId()).isEqualTo(11L);
        assertThat(actualizado.get().getStock()).isEqualTo(20);
        assertThat(vacio).isEmpty();
    }

    @Test
    void actualizarRechazaVideojuegoYaUsadoPorOtroInventario() {
        Inventario otro = inventario(2L, 11L, 3, 1);
        when(videoJuegoClient.buscarPorId(11L)).thenReturn(videojuego(11L, "Zelda"));
        when(inventarioRepository.findByVideojuegoId(11L)).thenReturn(Optional.of(otro));

        assertThatThrownBy(() -> inventarioService.actualizar(1L, new CrearInventarioRequest(11L, 20, 4)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Ya existe inventario");
    }

    @Test
    void operacionesDeStockActualizanCuandoExisteYRetornanVacioSiNo() {
        Inventario inventario = inventario(1L, 10L, 10, 2);
        when(inventarioRepository.findByVideojuegoId(10L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.findByVideojuegoId(99L)).thenReturn(Optional.empty());
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Inventario> fijado = inventarioService.actualizarStockPorVideojuego(10L, new ActualizarStockRequest(15));
        Optional<Inventario> aumentado = inventarioService.aumentarStock(10L, new MovimientoStockRequest(5));
        Optional<Inventario> disminuido = inventarioService.disminuirStock(10L, new MovimientoStockRequest(8));

        assertThat(fijado).isPresent();
        assertThat(fijado.get().getStock()).isEqualTo(12);
        assertThat(aumentado).isPresent();
        assertThat(disminuido).isPresent();
        assertThat(inventarioService.actualizarStockPorVideojuego(99L, new ActualizarStockRequest(1))).isEmpty();
        assertThat(inventarioService.aumentarStock(99L, new MovimientoStockRequest(1))).isEmpty();
        assertThat(inventarioService.disminuirStock(99L, new MovimientoStockRequest(1))).isEmpty();
    }

    @Test
    void disminuirStockRechazaStockInsuficiente() {
        when(inventarioRepository.findByVideojuegoId(10L)).thenReturn(Optional.of(inventario(1L, 10L, 2, 1)));

        assertThatThrownBy(() -> inventarioService.disminuirStock(10L, new MovimientoStockRequest(5)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    void eliminarRetornaTrueSoloCuandoExiste() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        when(inventarioRepository.existsById(99L)).thenReturn(false);

        assertThat(inventarioService.eliminar(1L)).isTrue();
        assertThat(inventarioService.eliminar(99L)).isFalse();
        verify(inventarioRepository).deleteById(1L);
        verify(inventarioRepository, never()).deleteById(99L);
    }

    @Test
    void inventarioLifecycleActualizaFecha() {
        Inventario inventario = inventario(1L, 10L, 5, 2);

        inventario.actualizarFecha();

        assertThat(inventario.getFechaActualizacion()).isNotNull();
    }

    private Inventario inventario(Long id, Long videojuegoId, Integer stock, Integer stockMinimo) {
        Inventario inventario = new Inventario();
        inventario.setId(id);
        inventario.setVideojuegoId(videojuegoId);
        inventario.setStock(stock);
        inventario.setStockMinimo(stockMinimo);
        return inventario;
    }

    private VideoJuegoResponse videojuego() {
        return videojuego(10L, "Minecraft");
    }

    private VideoJuegoResponse videojuego(Long id, String nombre) {
        return new VideoJuegoResponse(id, nombre, "Sandbox", 19990, "PC", null, "Mojang", null, null, true);
    }

    private FeignException feignStatus(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/videojuegos",
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
