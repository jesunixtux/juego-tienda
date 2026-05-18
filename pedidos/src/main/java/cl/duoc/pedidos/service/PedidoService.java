package cl.duoc.pedidos.service;

import cl.duoc.pedidos.client.UsuarioFeign;
import cl.duoc.pedidos.dto.PedidoDTO;
import cl.duoc.pedidos.dto.UsuarioDTO;
import cl.duoc.pedidos.mapper.PedidoMapper;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.repository.PedidoRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UsuarioFeign usuarioFeign;
    private final PedidoMapper pedidoMapper;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioFeign usuarioFeign, PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioFeign = usuarioFeign;
        this.pedidoMapper = pedidoMapper;
    }

    public List<Pedido> findAll(){
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> findById(Long id){
        return pedidoRepository.findById(id);
    }

    @Transactional
    public Pedido save(Pedido p){
        validarUsuario(p.getUsuarioId());

        return pedidoRepository.save(p);
    }

    @Transactional
    public Optional<Pedido> update(Long id, Pedido pedido){
        validarUsuario(pedido.getUsuarioId());

        return pedidoRepository.findById(id)
                .map(p -> {
                    p.setUsuarioId(pedido.getUsuarioId());
                    p.setNombreJuego(pedido.getNombreJuego());
                    p.setPrecio(pedido.getPrecio());
                    p.setFechaPedido(pedido.getFechaPedido());

                    return pedidoRepository.save(p);
                });
    }

    @Transactional
    public boolean delete(Long id){
        if (!pedidoRepository.existsById(id)) {
            return false;
        }

        pedidoRepository.deleteById(id);
        return true;
    }

    public List<PedidoDTO> findPedidosConUsuario(){
        List<Pedido> pedidos = pedidoRepository.findAll();

        return pedidos.stream().map(p -> {
            UsuarioDTO usuario = obtenerUsuario(p.getUsuarioId());
            return pedidoMapper.toDTO(p, usuario);
        }).toList();
    }

    public List<Pedido> findByUsuario(Long usuarioId){
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> findByFechaPedidoBetween(LocalDate desde, LocalDate hasta){
        return pedidoRepository.findByFechaPedidoBetween(desde, hasta);
    }

    public List<Pedido> findByPrecioBetween(Double minimo, Double maximo){
        return pedidoRepository.findByPrecioBetween(minimo, maximo);
    }

    private void validarUsuario(Long usuarioId) {
        obtenerUsuario(usuarioId);
    }

    private UsuarioDTO obtenerUsuario(Long usuarioId) {
        try {
            return usuarioFeign.obtenerUsuario(usuarioId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        } catch (FeignException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar el microservicio usuarios");
        }
    }
}
