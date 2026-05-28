package cl.duoc.carrito.service;

import cl.duoc.carrito.client.ResenaClient;
import cl.duoc.carrito.client.UsuarioClient;
import cl.duoc.carrito.client.VideoJuegoClient;
import cl.duoc.carrito.dto.ActualizarCantidadRequest;
import cl.duoc.carrito.dto.AgregarItemCarritoRequest;
import cl.duoc.carrito.dto.ItemCarritoResponse;
import cl.duoc.carrito.dto.ResenaCarritoResponse;
import cl.duoc.carrito.dto.ResenaResponse;
import cl.duoc.carrito.dto.ResumenCarritoResponse;
import cl.duoc.carrito.dto.UsuarioResponse;
import cl.duoc.carrito.dto.VideoJuegoResponse;
import cl.duoc.carrito.model.ItemCarrito;
import cl.duoc.carrito.repository.ItemCarritoRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {
    private final ItemCarritoRepository itemCarritoRepository;
    private final VideoJuegoClient videoJuegoClient;
    private final UsuarioClient usuarioClient;
    private final ResenaClient resenaClient;

    public CarritoService(
            ItemCarritoRepository itemCarritoRepository,
            VideoJuegoClient videoJuegoClient,
            UsuarioClient usuarioClient,
            ResenaClient resenaClient) {
        this.itemCarritoRepository = itemCarritoRepository;
        this.videoJuegoClient = videoJuegoClient;
        this.usuarioClient = usuarioClient;
        this.resenaClient = resenaClient;
    }

    public List<ItemCarrito> listarPorUsuario(Long usuarioId) {
        return itemCarritoRepository.findByUsuarioId(usuarioId);
    }

    public List<ItemCarritoResponse> listarPorUsuarioConVideojuego(Long usuarioId) {
        String nombreUsuario = obtenerNombreUsuario(usuarioId);
        List<ResenaResponse> resenasUsuario = obtenerResenasUsuario(usuarioId);
        return listarPorUsuario(usuarioId).stream()
                .map(item -> toResponse(item, nombreUsuario, resenasUsuario))
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
        String nombreUsuario = obtenerNombreUsuario(usuarioId);
        List<ResenaResponse> resenasUsuario = obtenerResenasUsuario(usuarioId);
        List<ItemCarritoResponse> itemsConVideojuego = items.stream()
                .map(item -> toResponse(item, nombreUsuario, resenasUsuario))
                .toList();
        Integer total = items.stream()
                .map(ItemCarrito::getSubtotal)
                .filter(subtotal -> subtotal != null)
                .reduce(0, Integer::sum);

        return new ResumenCarritoResponse(nombreUsuario, itemsConVideojuego, total, usuarioId);
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
        return toResponse(item, obtenerNombreUsuario(item.getUsuarioId()), obtenerResenasUsuario(item.getUsuarioId()));
    }

    private ItemCarritoResponse toResponse(ItemCarrito item, String nombreUsuario, List<ResenaResponse> resenasUsuario) {
        String nombreVideojuego = obtenerNombreVideojuego(item.getVideojuegoId());
        ResenaCarritoResponse resena = buscarResenaDelUsuario(resenasUsuario, nombreVideojuego);

        return new ItemCarritoResponse(
                nombreUsuario,
                nombreVideojuego,
                resena,
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal(),
                item.getFechaAgregado(),
                item.getId(),
                item.getUsuarioId(),
                item.getVideojuegoId()
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

    private List<ResenaResponse> obtenerResenasUsuario(Long usuarioId) {
        try {
            List<ResenaResponse> resenas = resenaClient.listarPorUsuario(usuarioId);
            return resenas == null ? List.of() : resenas;
        } catch (FeignException exception) {
            return List.of();
        }
    }

    private ResenaCarritoResponse buscarResenaDelUsuario(List<ResenaResponse> resenasUsuario, String nombreVideojuego) {
        if (nombreVideojuego == null || nombreVideojuego.isBlank()) {
            return null;
        }

        return resenasUsuario.stream()
                .filter(resena -> mismoNombreJuego(resena.nombreJuego(), nombreVideojuego))
                .max(Comparator.comparing(
                        ResenaResponse::fechaResena,
                        Comparator.nullsFirst(Comparator.naturalOrder())))
                .map(this::toResenaCarritoResponse)
                .orElse(null);
    }

    private boolean mismoNombreJuego(String nombreResena, String nombreVideojuego) {
        return nombreResena != null
                && nombreResena.trim().equalsIgnoreCase(nombreVideojuego.trim());
    }

    private ResenaCarritoResponse toResenaCarritoResponse(ResenaResponse resena) {
        return new ResenaCarritoResponse(
                resena.id(),
                resena.comentario(),
                resena.puntuacion(),
                resena.fechaResena()
        );
    }

    private String obtenerNombreUsuario(Long usuarioId) {
        try {
            UsuarioResponse usuario = usuarioClient.buscarPorId(usuarioId);
            return formatearNombreUsuario(usuario);
        } catch (FeignException.NotFound exception) {
            return "Usuario no encontrado";
        } catch (FeignException exception) {
            return "Usuario no disponible";
        }
    }

    private String formatearNombreUsuario(UsuarioResponse usuario) {
        String nombre = usuario.nombre() == null ? "" : usuario.nombre().trim();
        String apellido = usuario.apellido() == null ? "" : usuario.apellido().trim();
        String nombreCompleto = (nombre + " " + apellido).trim();

        if (!nombreCompleto.isBlank()) {
            return nombreCompleto;
        }

        if (usuario.correo() != null && !usuario.correo().isBlank()) {
            return usuario.correo();
        }

        return "Usuario " + usuario.id();
    }
}
