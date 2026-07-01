package cl.duoc.resenas.service;

import cl.duoc.resenas.client.UsuarioFeign;
import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.dto.UsuarioDTO;
import cl.duoc.resenas.exception.ExternalServiceException;
import cl.duoc.resenas.exception.ResourceNotFoundException;
import cl.duoc.resenas.mapper.ResenaMapper;
import cl.duoc.resenas.model.Resena;
import cl.duoc.resenas.repository.ResenaRepository;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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
class ResenaServiceBranchTests {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private UsuarioFeign usuarioFeign;

    @Spy
    private ResenaMapper resenaMapper;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    void findAllFindByIdYFechaConDetalleEnriquecenUsuario() {
        Resena resena = resena(1L, 2L, "Minecraft");
        when(resenaRepository.findAll()).thenReturn(List.of(resena));
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        when(resenaRepository.findByFechaResenaBetween(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30)))
                .thenReturn(List.of(resena));
        when(usuarioFeign.obtenerUsuario(2L)).thenReturn(usuario());

        List<ResenaDTO> todas = resenaService.findAllConUsuario();
        Optional<ResenaDTO> porId = resenaService.findByIdConUsuario(1L);
        List<ResenaDTO> porFecha = resenaService.findByFechaConDetalle(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30));

        assertThat(todas.getFirst().getNombreUsuario()).isEqualTo("Jesus Emilio");
        assertThat(porId).isPresent();
        assertThat(porId.get().getCorreoUsuario()).isEqualTo("jesus@tiendajuegos.cl");
        assertThat(porFecha.getFirst().getNombreJuego()).isEqualTo("Minecraft");
    }

    @Test
    void updateActualizaCamposOVuelveVacio() {
        Resena existente = resena(1L, 2L, "Minecraft");
        Resena datos = resena(null, 2L, "Zelda");
        datos.setComentario("Muy buena aventura");
        datos.setPuntuacion(4);
        datos.setFechaResena(LocalDate.of(2026, 7, 1));
        when(usuarioFeign.obtenerUsuario(2L)).thenReturn(usuario());
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());
        when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Resena> actualizada = resenaService.update(1L, datos);
        Optional<Resena> vacia = resenaService.update(99L, datos);

        assertThat(actualizada).isPresent();
        assertThat(actualizada.get().getNombreJuego()).isEqualTo("Zelda");
        assertThat(actualizada.get().getComentario()).isEqualTo("Muy buena aventura");
        assertThat(actualizada.get().getPuntuacion()).isEqualTo(4);
        assertThat(vacia).isEmpty();
    }

    @Test
    void deleteRetornaTrueSoloCuandoExiste() {
        when(resenaRepository.existsById(1L)).thenReturn(true);
        when(resenaRepository.existsById(99L)).thenReturn(false);

        assertThat(resenaService.delete(1L)).isTrue();
        assertThat(resenaService.delete(99L)).isFalse();
        verify(resenaRepository).deleteById(1L);
        verify(resenaRepository, never()).deleteById(99L);
    }

    @Test
    void saveTraduceErroresDelMicroservicioUsuarios() {
        Resena noEncontrada = resena(1L, 404L, "Minecraft");
        Resena noDisponible = resena(2L, 500L, "Zelda");
        when(usuarioFeign.obtenerUsuario(404L)).thenThrow(feignStatus(404));
        when(usuarioFeign.obtenerUsuario(500L)).thenThrow(feignStatus(500));

        assertThatThrownBy(() -> resenaService.save(noEncontrada))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
        assertThatThrownBy(() -> resenaService.save(noDisponible))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("microservicio usuarios");
    }

    @Test
    void findConUsuarioYFiltrosDeleganEnRepositorio() {
        Resena resena = resena(1L, 2L, "Minecraft");
        when(resenaRepository.findAll()).thenReturn(List.of(resena));
        when(resenaRepository.findByUsuarioId(2L)).thenReturn(List.of(resena));
        when(resenaRepository.findByPuntuacionBetween(4, 5)).thenReturn(List.of(resena));
        when(usuarioFeign.obtenerUsuario(2L)).thenReturn(usuario());

        assertThat(resenaService.findConUsuario()).hasSize(1);
        assertThat(resenaService.findByUsuario(2L)).containsExactly(resena);
        assertThat(resenaService.findByPuntuacionConDetalle(4, 5).getFirst().getNombreUsuario())
                .isEqualTo("Jesus Emilio");
    }

    private Resena resena(Long id, Long usuarioId, String nombreJuego) {
        Resena resena = new Resena();
        resena.setId(id);
        resena.setUsuarioId(usuarioId);
        resena.setNombreJuego(nombreJuego);
        resena.setComentario("Muy entretenido");
        resena.setPuntuacion(5);
        resena.setFechaResena(LocalDate.of(2026, 6, 20));
        return resena;
    }

    private UsuarioDTO usuario() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setId(2L);
        usuario.setNombre("Jesus");
        usuario.setApellido("Emilio");
        usuario.setCorreo("jesus@tiendajuegos.cl");
        return usuario;
    }

    private FeignException feignStatus(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/usuarios",
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
