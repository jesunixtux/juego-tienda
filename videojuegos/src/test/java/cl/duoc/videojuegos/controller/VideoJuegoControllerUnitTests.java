package cl.duoc.videojuegos.controller;

import cl.duoc.videojuegos.model.VideoJuego;
import cl.duoc.videojuegos.service.VideoJuegoService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VideoJuegoControllerUnitTests {

    private final VideoJuegoService videoJuegoService = mock(VideoJuegoService.class);
    private final VideoJuegoController controller = new VideoJuegoController(videoJuegoService);

    @Test
    void listarYBuscarPorIdRetornanRespuestasEsperadas() {
        VideoJuego juego = juego(1L);
        when(videoJuegoService.listar()).thenReturn(List.of(juego));
        when(videoJuegoService.buscarPorId(1L)).thenReturn(Optional.of(juego));
        when(videoJuegoService.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThat(controller.listar().getBody()).containsExactly(juego);
        assertThat(controller.buscarPorId(1L).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.buscarPorId(99L).getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void crearActualizarYEliminarDeleganEnService() {
        VideoJuego juego = juego(2L);
        when(videoJuegoService.crear(juego)).thenReturn(juego);
        when(videoJuegoService.actualizar(2L, juego)).thenReturn(Optional.of(juego));
        when(videoJuegoService.actualizar(99L, juego)).thenReturn(Optional.empty());
        when(videoJuegoService.eliminar(2L)).thenReturn(true);
        when(videoJuegoService.eliminar(99L)).thenReturn(false);

        assertThat(controller.crear(juego).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.actualizar(2L, juego).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.actualizar(99L, juego).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.eliminar(2L).getStatusCode().value()).isEqualTo(204);
        assertThat(controller.eliminar(99L).getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void buscarUsaFiltroCorrectoSegunParametros() {
        when(videoJuegoService.buscarPorNombre("mine")).thenReturn(List.of(juego(1L)));
        when(videoJuegoService.buscarPorCategoria("RPG")).thenReturn(List.of(juego(2L)));
        when(videoJuegoService.buscarPorPlataforma("PC")).thenReturn(List.of(juego(3L)));
        when(videoJuegoService.buscarPorPrecio(10000, 20000)).thenReturn(List.of(juego(4L)));
        when(videoJuegoService.listar()).thenReturn(List.of(juego(5L)));

        controller.buscar("mine", null, null, null, null);
        controller.buscar(null, "RPG", null, null, null);
        controller.buscar(null, null, "PC", null, null);
        controller.buscar(null, null, null, 10000, 20000);
        ResponseEntity<List<VideoJuego>> sinFiltro = controller.buscar(null, null, null, null, null);

        assertThat(sinFiltro.getStatusCode().value()).isEqualTo(200);
        verify(videoJuegoService).buscarPorNombre("mine");
        verify(videoJuegoService).buscarPorCategoria("RPG");
        verify(videoJuegoService).buscarPorPlataforma("PC");
        verify(videoJuegoService).buscarPorPrecio(10000, 20000);
        verify(videoJuegoService).listar();
    }

    private VideoJuego juego(Long id) {
        VideoJuego juego = new VideoJuego();
        juego.setId(id);
        juego.setNombre("Minecraft");
        juego.setCategoria("Sandbox");
        juego.setPrecio(19990);
        juego.setPlataforma("PC");
        return juego;
    }
}
