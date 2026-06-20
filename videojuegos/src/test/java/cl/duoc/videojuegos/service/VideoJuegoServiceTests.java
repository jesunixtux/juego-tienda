package cl.duoc.videojuegos.service;

import cl.duoc.videojuegos.exception.PlataformaException;
import cl.duoc.videojuegos.model.VideoJuego;
import cl.duoc.videojuegos.repository.VideoJuegoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoJuegoServiceTests {

    @Mock
    private VideoJuegoRepository videoJuegoRepository;

    @InjectMocks
    private VideoJuegoService videoJuegoService;

    @Test
    void crearNormalizaPlataformaPermitidaYActivaPorDefecto() {
        VideoJuego videoJuego = new VideoJuego();
        videoJuego.setNombre("Forza Horizon 5");
        videoJuego.setCategoria("Carreras");
        videoJuego.setPrecio(39990);
        videoJuego.setPlataforma(" PC ");

        when(videoJuegoRepository.save(videoJuego)).thenReturn(videoJuego);

        VideoJuego creado = videoJuegoService.crear(videoJuego);

        assertThat(creado.getPlataforma()).isEqualTo("PC");
        assertThat(creado.getActivo()).isTrue();
        verify(videoJuegoRepository).save(videoJuego);
    }

    @Test
    void crearRechazaPlataformaNoPermitida() {
        VideoJuego videoJuego = new VideoJuego();
        videoJuego.setPlataforma("Game Boy");

        assertThatThrownBy(() -> videoJuegoService.crear(videoJuego))
                .isInstanceOf(PlataformaException.class)
                .hasMessageContaining("Plataforma no valida")
                .hasMessageContaining("PS4")
                .hasMessageContaining("XBOX")
                .hasMessageContaining("PC");
    }

    @Test
    void buscarPorPrecioUsaRangoCuandoMinimoYMaximoExisten() {
        VideoJuego minecraft = new VideoJuego();
        minecraft.setNombre("Minecraft");
        when(videoJuegoRepository.findByPrecioBetween(10000, 30000)).thenReturn(List.of(minecraft));

        List<VideoJuego> resultado = videoJuegoService.buscarPorPrecio(10000, 30000);

        assertThat(resultado).containsExactly(minecraft);
        verify(videoJuegoRepository).findByPrecioBetween(10000, 30000);
    }
}
