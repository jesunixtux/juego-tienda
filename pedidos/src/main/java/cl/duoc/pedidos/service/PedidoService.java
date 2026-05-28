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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<PedidoDTO> findAllConDetalle() {
        return toDTOs(findAll());
    }

    public Optional<Pedido> findById(Long id){
        return pedidoRepository.findById(id);
    }

    public Optional<PedidoDTO> findByIdConDetalle(Long id) {
        return findById(id).map(this::toDTO);
    }

    @Transactional
    public Pedido save(Pedido p){
        validarUsuario(p.getUsuarioId());

        return pedidoRepository.save(p);
    }

    @Transactional
    public PedidoDTO saveConDetalle(Pedido p) {
        return toDTO(save(p));
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
    public Optional<PedidoDTO> updateConDetalle(Long id, Pedido pedido) {
        return update(id, pedido).map(this::toDTO);
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
        return findAllConDetalle();
    }

    public List<Pedido> findByUsuario(Long usuarioId){
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<PedidoDTO> findByUsuarioConDetalle(Long usuarioId) {
        return toDTOs(findByUsuario(usuarioId));
    }

    public List<Pedido> findByFechaPedidoBetween(LocalDate desde, LocalDate hasta){
        return pedidoRepository.findByFechaPedidoBetween(desde, hasta);
    }

    public List<PedidoDTO> findByFechaPedidoBetweenConDetalle(LocalDate desde, LocalDate hasta) {
        return toDTOs(findByFechaPedidoBetween(desde, hasta));
    }

    public List<Pedido> findByPrecioBetween(Double minimo, Double maximo){
        return pedidoRepository.findByPrecioBetween(minimo, maximo);
    }

    public List<PedidoDTO> findByPrecioBetweenConDetalle(Double minimo, Double maximo) {
        return toDTOs(findByPrecioBetween(minimo, maximo));
    }

    private void validarUsuario(Long usuarioId) {
        obtenerUsuario(usuarioId);
    }

    private UsuarioDTO obtenerUsuario(Long usuarioId) {
        try {
            return usuarioFeign.obtenerUsuario(usuarioId);
        } catch (FeignException.NotFound exception) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        } catch (FeignException exception) {
            throw new ExternalServiceException("No se pudo consultar el microservicio usuarios");
        }
    }

    private PedidoDTO toDTO(Pedido pedido) {
        UsuarioDTO usuario = obtenerUsuario(pedido.getUsuarioId());
        return pedidoMapper.toDTO(pedido, usuario);
    }

    private List<PedidoDTO> toDTOs(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(this::toDTO)
                .toList();
    }
}
