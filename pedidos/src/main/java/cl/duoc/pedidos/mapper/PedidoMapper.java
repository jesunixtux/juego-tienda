package cl.duoc.pedidos.mapper;

import cl.duoc.pedidos.dto.PedidoDTO;
import cl.duoc.pedidos.dto.UsuarioDTO;
import cl.duoc.pedidos.model.Pedido;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public PedidoDTO toDTO(Pedido pedido, UsuarioDTO usuario){
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setUsuarioId(pedido.getUsuarioId());
        dto.setNombreJuego(pedido.getNombreJuego());
        dto.setPrecio(pedido.getPrecio());
        dto.setFechaPedido(pedido.getFechaPedido());

        if(usuario != null){
            dto.setNombreUsuario(usuario.getNombre() + " " + usuario.getApellido());
            dto.setCorreoUsuario(usuario.getCorreo());
        }

        return dto;
    }
}
