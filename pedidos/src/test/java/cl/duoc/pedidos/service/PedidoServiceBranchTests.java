package cl.duoc.pedidos.service;

import cl.duoc.pedidos.client.UsuarioFeign;
import cl.duoc.pedidos.dto.PedidoDTO;
import cl.duoc.pedidos.dto.UsuarioDTO;
import cl.duoc.pedidos.exception.ExternalServiceException;
import cl.duoc.pedidos.exception.ResourceNotFoundException;
import cl.duoc.pedidos.mapper.PedidoMapper;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.repository.PedidoRepository;
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
class PedidoServiceBranchTests {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioFeign usuarioFeign;

    @Spy
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void findByIdConDetalleYFiltrosEnriquecenUsuario() {
        Pedido pedido = pedido(1L, 2L, "Minecraft");
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.findByFechaPedidoBetween(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30)))
                .thenReturn(List.of(pedido));
        when(pedidoRepository.findByUsuarioId(2L)).thenReturn(List.of(pedido));
        when(usuarioFeign.obtenerUsuario(2L)).thenReturn(usuario());

        Optional<PedidoDTO> porId = pedidoService.findByIdConDetalle(1L);
        List<PedidoDTO> porFecha = pedidoService.findByFechaPedidoBetweenConDetalle(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30));
        List<PedidoDTO> porUsuario = pedidoService.findByUsuarioConDetalle(2L);

        assertThat(porId).isPresent();
        assertThat(porId.get().getNombreUsuario()).isEqualTo("Jesus Emilio");
        assertThat(porFecha.getFirst().getCorreoUsuario()).isEqualTo("jesus@tiendajuegos.cl");
        assertThat(porUsuario.getFirst().getNombreJuego()).isEqualTo("Minecraft");
    }

    @Test
    void updateActualizaCamposOVuelveVacio() {
        Pedido existente = pedido(1L, 2L, "Minecraft");
        Pedido datos = pedido(null, 2L, "Zelda");
        datos.setPrecio(49990.0);
        datos.setFechaPedido(LocalDate.of(2026, 7, 1));
        when(usuarioFeign.obtenerUsuario(2L)).thenReturn(usuario());
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Pedido> actualizado = pedidoService.update(1L, datos);
        Optional<Pedido> vacio = pedidoService.update(99L, datos);

        assertThat(actualizado).isPresent();
        assertThat(actualizado.get().getNombreJuego()).isEqualTo("Zelda");
        assertThat(actualizado.get().getPrecio()).isEqualTo(49990.0);
        assertThat(vacio).isEmpty();
    }

    @Test
    void deleteRetornaTrueSoloCuandoExiste() {
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        assertThat(pedidoService.delete(1L)).isTrue();
        assertThat(pedidoService.delete(99L)).isFalse();
        verify(pedidoRepository).deleteById(1L);
        verify(pedidoRepository, never()).deleteById(99L);
    }

    @Test
    void saveTraduceErroresDelMicroservicioUsuarios() {
        Pedido noEncontrado = pedido(1L, 404L, "Minecraft");
        Pedido noDisponible = pedido(2L, 500L, "Zelda");
        when(usuarioFeign.obtenerUsuario(404L)).thenThrow(feignStatus(404));
        when(usuarioFeign.obtenerUsuario(500L)).thenThrow(feignStatus(500));

        assertThatThrownBy(() -> pedidoService.save(noEncontrado))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
        assertThatThrownBy(() -> pedidoService.save(noDisponible))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("microservicio usuarios");
    }

    @Test
    void findByUsuarioYPrecioDeleganEnRepositorio() {
        Pedido pedido = pedido(1L, 2L, "Minecraft");
        when(pedidoRepository.findByUsuarioId(2L)).thenReturn(List.of(pedido));
        when(pedidoRepository.findByPrecioBetween(10000.0, 30000.0)).thenReturn(List.of(pedido));

        assertThat(pedidoService.findByUsuario(2L)).containsExactly(pedido);
        assertThat(pedidoService.findByPrecioBetween(10000.0, 30000.0)).containsExactly(pedido);
    }

    private Pedido pedido(Long id, Long usuarioId, String nombreJuego) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setUsuarioId(usuarioId);
        pedido.setNombreJuego(nombreJuego);
        pedido.setPrecio(19990.0);
        pedido.setFechaPedido(LocalDate.of(2026, 6, 20));
        return pedido;
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
