package cl.duoc.pedidos.service;

import cl.duoc.pedidos.client.UsuarioFeign;
import cl.duoc.pedidos.dto.PedidoDTO;
import cl.duoc.pedidos.dto.UsuarioDTO;
import cl.duoc.pedidos.mapper.PedidoMapper;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTests {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioFeign usuarioFeign;

    @Spy
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void saveValidaUsuarioAntesDeGuardar() {
        Pedido pedido = pedido();
        when(usuarioFeign.obtenerUsuario(2L)).thenReturn(usuario());
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido guardado = pedidoService.save(pedido);

        assertThat(guardado.getNombreJuego()).isEqualTo("Minecraft");
        verify(usuarioFeign).obtenerUsuario(2L);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void findAllConDetalleEnriqueceNombreYCorreoDelUsuario() {
        Pedido pedido = pedido();
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));
        when(usuarioFeign.obtenerUsuario(2L)).thenReturn(usuario());

        List<PedidoDTO> resultado = pedidoService.findAllConDetalle();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getNombreUsuario()).isEqualTo("Jesus Emilio");
        assertThat(resultado.getFirst().getCorreoUsuario()).isEqualTo("jesus@tiendajuegos.cl");
    }

    @Test
    void findByPrecioBetweenDelegaEnRepositorio() {
        Pedido pedido = pedido();
        when(pedidoRepository.findByPrecioBetween(10000.0, 30000.0)).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.findByPrecioBetween(10000.0, 30000.0);

        assertThat(resultado).containsExactly(pedido);
        verify(pedidoRepository).findByPrecioBetween(10000.0, 30000.0);
    }

    private Pedido pedido() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuarioId(2L);
        pedido.setNombreJuego("Minecraft");
        pedido.setPrecio(19990.0);
        pedido.setFechaPedido(LocalDate.of(2026, 5, 20));
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
}
