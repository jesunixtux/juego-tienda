package cl.duoc.inventario.service;

import cl.duoc.inventario.client.VideoJuegoClient;
import cl.duoc.inventario.dto.CrearInventarioRequest;
import cl.duoc.inventario.dto.InventarioResponse;
import cl.duoc.inventario.dto.MovimientoStockRequest;
import cl.duoc.inventario.dto.VideoJuegoResponse;
import cl.duoc.inventario.exception.ConflictException;
import cl.duoc.inventario.model.Inventario;
import cl.duoc.inventario.repository.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTests {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private VideoJuegoClient videoJuegoClient;

    @InjectMocks
    private InventarioService inventarioService;

    @Test
    void crearValidaVideojuegoYEvitaDuplicados() {
        CrearInventarioRequest request = new CrearInventarioRequest(10L, 8, 2);
        when(videoJuegoClient.buscarPorId(10L)).thenReturn(videojuego());
        when(inventarioRepository.existsByVideojuegoId(10L)).thenReturn(false);
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inventario inventario = inventarioService.crear(request);

        assertThat(inventario.getVideojuegoId()).isEqualTo(10L);
        assertThat(inventario.getStock()).isEqualTo(8);
        assertThat(inventario.getStockMinimo()).isEqualTo(2);
        verify(videoJuegoClient).buscarPorId(10L);
    }

    @Test
    void crearRechazaInventarioDuplicadoParaMismoVideojuego() {
        when(videoJuegoClient.buscarPorId(10L)).thenReturn(videojuego());
        when(inventarioRepository.existsByVideojuegoId(10L)).thenReturn(true);

        assertThatThrownBy(() -> inventarioService.crear(new CrearInventarioRequest(10L, 8, 2)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Ya existe inventario");
    }

    @Test
    void disminuirStockRechazaCantidadMayorAlDisponible() {
        Inventario inventario = inventario(1L, 10L, 3, 2);
        when(inventarioRepository.findByVideojuegoId(10L)).thenReturn(Optional.of(inventario));

        assertThatThrownBy(() -> inventarioService.disminuirStock(10L, new MovimientoStockRequest(5)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    void listarBajoStockConVideojuegoSoloRetornaRegistrosCriticos() {
        Inventario bajoStock = inventario(1L, 10L, 2, 2);
        Inventario stockOk = inventario(2L, 11L, 8, 2);
        when(inventarioRepository.findAll()).thenReturn(List.of(bajoStock, stockOk));
        when(videoJuegoClient.buscarPorId(10L)).thenReturn(videojuego());

        List<InventarioResponse> resultado = inventarioService.listarBajoStockConVideojuego();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nombreVideojuego()).isEqualTo("Minecraft");
        assertThat(resultado.getFirst().stock()).isEqualTo(2);
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
        return new VideoJuegoResponse(10L, "Minecraft", "Sandbox", 19990, "PC", null, "Mojang", null, null, true);
    }
}
