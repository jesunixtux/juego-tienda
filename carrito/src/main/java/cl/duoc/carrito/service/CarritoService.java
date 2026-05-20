package cl.duoc.carrito.service;

import cl.duoc.carrito.client.VideoJuegoClient;
import cl.duoc.carrito.dto.ActualizarCantidadRequest;
import cl.duoc.carrito.dto.AgregarItemCarritoRequest;
import cl.duoc.carrito.dto.ItemCarritoResponse;
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

    public List<ItemCarritoResponse> listarPorUsuarioConVideojuego(Long usuarioId) {
        return listarPorUsuario(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ItemCarrito> buscarPorId(Long id) {
        return itemCarritoRepository.findById(id);
    }

    public Optional<ItemCarritoResponse> buscarPorIdConVideojuego(Long id) {
        return buscarPorId(id).map(this::toResponse);
    }

    public ResumenCarritoResponse obtenerResumen(Long usuarioId) {
        List<ItemCarrito> items = listarPorUsuario(usuarioId);
        List<ItemCarritoResponse> itemsConVideojuego = items.stream()
                .map(this::toResponse)
                .toList();
        Integer total = items.stream()
                .map(ItemCarrito::getSubtotal)
                .filter(subtotal -> subtotal != null)
                .reduce(0, Integer::sum);

        return new ResumenCarritoResponse(usuarioId, itemsConVideojuego, total);
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
    public ItemCarritoResponse agregarConVideojuego(AgregarItemCarritoRequest request) {
        return toResponse(agregar(request));
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
    public Optional<ItemCarritoResponse> actualizarCantidadConVideojuego(Long id, ActualizarCantidadRequest request) {
        return actualizarCantidad(id, request).map(this::toResponse);
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

    private ItemCarritoResponse toResponse(ItemCarrito item) {
        String nombreVideojuego = obtenerNombreVideojuego(item.getVideojuegoId());

        return new ItemCarritoResponse(
                item.getId(),
                item.getUsuarioId(),
                item.getVideojuegoId(),
                nombreVideojuego,
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal(),
                item.getFechaAgregado()
        );
    }

    private String obtenerNombreVideojuego(Long videojuegoId) {
        try {
            return videoJuegoClient.buscarPorId(videojuegoId).nombre();
        } catch (FeignException.NotFound exception) {
            return "Videojuego no encontrado";
        } catch (FeignException exception) {
            return "Videojuego no disponible";
        }
    }
}
