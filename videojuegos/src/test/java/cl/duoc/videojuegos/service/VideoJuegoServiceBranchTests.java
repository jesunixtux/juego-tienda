package cl.duoc.videojuegos.service;

import cl.duoc.videojuegos.model.VideoJuego;
import cl.duoc.videojuegos.repository.VideoJuegoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoJuegoServiceBranchTests {

    @Mock
    private VideoJuegoRepository videoJuegoRepository;

    @InjectMocks
    private VideoJuegoService videoJuegoService;

    @Test
    void listarYBusquedasDeleganEnRepositorio() {
        VideoJuego minecraft = videojuego("Minecraft", "Sandbox", 19990, "PC");
        when(videoJuegoRepository.findAll()).thenReturn(List.of(minecraft));
        when(videoJuegoRepository.findById(1L)).thenReturn(Optional.of(minecraft));
        when(videoJuegoRepository.findByNombreContainingIgnoreCase("mine")).thenReturn(List.of(minecraft));
        when(videoJuegoRepository.findByCategoriaIgnoreCase("Sandbox")).thenReturn(List.of(minecraft));
        when(videoJuegoRepository.findByPlataformaIgnoreCase("PC")).thenReturn(List.of(minecraft));

        assertThat(videoJuegoService.listar()).containsExactly(minecraft);
        assertThat(videoJuegoService.buscarPorId(1L)).contains(minecraft);
        assertThat(videoJuegoService.buscarPorNombre("mine")).containsExactly(minecraft);
        assertThat(videoJuegoService.buscarPorCategoria("Sandbox")).containsExactly(minecraft);
        assertThat(videoJuegoService.buscarPorPlataforma("PC")).containsExactly(minecraft);
    }

    @Test
    void actualizarCopiaCamposNormalizaPlataformaYActivaPorDefecto() {
        VideoJuego existente = videojuego("Antiguo", "Accion", 1000, "PC");
        VideoJuego datos = videojuego("God of War Ragnarok", "Aventura", 59990, " PS4 ");
        datos.setDescripcion("Historia nordica");
        datos.setDesarrollador("Santa Monica Studio");
        datos.setFechaLanzamiento(LocalDate.of(2022, 11, 9));
        datos.setImagenUrl("https://example.com/gow.jpg");
        datos.setActivo(null);

        when(videoJuegoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(videoJuegoRepository.save(any(VideoJuego.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<VideoJuego> actualizado = videoJuegoService.actualizar(1L, datos);

        assertThat(actualizado).isPresent();
        assertThat(actualizado.get().getNombre()).isEqualTo("God of War Ragnarok");
        assertThat(actualizado.get().getCategoria()).isEqualTo("Aventura");
        assertThat(actualizado.get().getPrecio()).isEqualTo(59990);
        assertThat(actualizado.get().getPlataforma()).isEqualTo("PS4");
        assertThat(actualizado.get().getActivo()).isTrue();
        verify(videoJuegoRepository).save(existente);
    }

    @Test
    void actualizarRetornaVacioSiNoExiste() {
        when(videoJuegoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<VideoJuego> resultado = videoJuegoService.actualizar(99L, videojuego("Minecraft", "Sandbox", 19990, "PC"));

        assertThat(resultado).isEmpty();
        verify(videoJuegoRepository, never()).save(any());
    }

    @Test
    void eliminarRetornaTrueSoloCuandoExiste() {
        when(videoJuegoRepository.existsById(1L)).thenReturn(true);
        when(videoJuegoRepository.existsById(99L)).thenReturn(false);

        assertThat(videoJuegoService.eliminar(1L)).isTrue();
        assertThat(videoJuegoService.eliminar(99L)).isFalse();
        verify(videoJuegoRepository).deleteById(1L);
    }

    @Test
    void buscarPorPrecioSoportaSoloMinimoOSoloMaximo() {
        VideoJuego barato = videojuego("Stardew Valley", "Simulacion", 8990, "PC");
        VideoJuego premium = videojuego("Final Fantasy VII Rebirth", "RPG", 69990, "PS5");
        when(videoJuegoRepository.findByPrecioGreaterThanEqual(50000)).thenReturn(List.of(premium));
        when(videoJuegoRepository.findByPrecioLessThanEqual(10000)).thenReturn(List.of(barato));

        assertThat(videoJuegoService.buscarPorPrecio(50000, null)).containsExactly(premium);
        assertThat(videoJuegoService.buscarPorPrecio(null, 10000)).containsExactly(barato);
    }

    @Test
    void crearPermitePlataformaVaciaSinCambiarla() {
        VideoJuego juego = videojuego("Juego retro", "Arcade", 5000, " ");
        when(videoJuegoRepository.save(juego)).thenReturn(juego);

        VideoJuego creado = videoJuegoService.crear(juego);

        assertThat(creado.getPlataforma()).isBlank();
    }

    private VideoJuego videojuego(String nombre, String categoria, Integer precio, String plataforma) {
        VideoJuego videoJuego = new VideoJuego();
        videoJuego.setNombre(nombre);
        videoJuego.setCategoria(categoria);
        videoJuego.setPrecio(precio);
        videoJuego.setPlataforma(plataforma);
        videoJuego.setActivo(true);
        return videoJuego;
    }
}
