package cl.duoc.carrito.service;

import cl.duoc.carrito.client.VideoJuegoClient;
import cl.duoc.carrito.dto.ActualizarCantidadRequest;
import cl.duoc.carrito.dto.AgregarItemCarritoRequest;
import cl.duoc.carrito.dto.ResumenCarritoResponse;
import cl.duoc.carrito.dto.VideoJuegoResponse;
import cl.duoc.carrito.model.ItemCarrito;
import cl.duoc.carrito.repository.ItemCarritoRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {
    private final ItemCarritoRepository itemCarritoRepository;
    private final VideoJuegoClient videoJuegoClient;

    public CarritoService(ItemCarritoRepository itemCarritoRepository, VideoJuegoClient videoJuegoClient) {
        this.itemCarritoRepository = itemCarritoRepository;
        this.videoJuegoClient = videoJuegoClient;
    }

    public List<ItemCarrito> listarPorUsuario(Long usuarioId) {
        return itemCarritoRepository.findByUsuarioId(usuarioId);
    }

    public Optional<ItemCarrito> buscarPorId(Long id) {
        return itemCarritoRepository.findById(id);
    }

    public ResumenCarritoResponse obtenerResumen(Long usuarioId) {
        List<ItemCarrito> items = listarPorUsuario(usuarioId);
        Integer total = items.stream()
                .map(ItemCarrito::getSubtotal)
                .filter(subtotal -> subtotal != null)
                .reduce(0, Integer::sum);

        return new ResumenCarritoResponse(usuarioId, items, total);
    }

    @Transactional
    public ItemCarrito agregar(AgregarItemCarritoRequest request) {
        VideoJuegoResponse videoJuego = obtenerVideoJuego(request.videojuegoId());
        Integer precioUnitario = obtenerPrecio(videoJuego);

        return itemCarritoRepository.findByUsuarioIdAndVideojuegoId(request.usuarioId(), request.videojuegoId())
                .map(item -> {
                    item.setCantidad(item.getCantidad() + request.cantidad());
                    item.setPrecioUnitario(precioUnitario);
                    return itemCarritoRepository.save(item);
                })
                .orElseGet(() -> {
                    ItemCarrito item = new ItemCarrito();
                    item.setUsuarioId(request.usuarioId());
                    item.setVideojuegoId(request.videojuegoId());
                    item.setCantidad(request.cantidad());
                    item.setPrecioUnitario(precioUnitario);

                    return itemCarritoRepository.save(item);
                });
    }

    @Transactional
    public Optional<ItemCarrito> actualizarCantidad(Long id, ActualizarCantidadRequest request) {
        return itemCarritoRepository.findById(id)
                .map(item -> {
                    item.setCantidad(request.cantidad());
                    return itemCarritoRepository.save(item);
                });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!itemCarritoRepository.existsById(id)) {
            return false;
        }

        itemCarritoRepository.deleteById(id);
        return true;
    }

    @Transactional
    public void vaciarPorUsuario(Long usuarioId) {
        itemCarritoRepository.deleteByUsuarioId(usuarioId);
    }

    private VideoJuegoResponse obtenerVideoJuego(Long videojuegoId) {
        try {
            return videoJuegoClient.buscarPorId(videojuegoId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Videojuego no encontrado");
        } catch (FeignException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar el microservicio videojuegos");
        }
    }

    private Integer obtenerPrecio(VideoJuegoResponse videoJuego) {
        if (videoJuego.precio() == null || videoJuego.precio() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "El videojuego no tiene un precio valido");
        }

        return videoJuego.precio();
    }
}
