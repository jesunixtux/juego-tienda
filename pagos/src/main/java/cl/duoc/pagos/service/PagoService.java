package cl.duoc.pagos.service;

import cl.duoc.pagos.client.CarritoClient;
import cl.duoc.pagos.client.UsuarioClient;
import cl.duoc.pagos.dto.ActualizarEstadoPagoRequest;
import cl.duoc.pagos.dto.CrearPagoRequest;
import cl.duoc.pagos.dto.PagoResponse;
import cl.duoc.pagos.dto.ResumenCarritoResponse;
import cl.duoc.pagos.dto.UsuarioResponse;
import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.repository.PagoRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    private final CarritoClient carritoClient;
    private final UsuarioClient usuarioClient;

    public PagoService(PagoRepository pagoRepository, CarritoClient carritoClient, UsuarioClient usuarioClient) {
        this.pagoRepository = pagoRepository;
        this.carritoClient = carritoClient;
        this.usuarioClient = usuarioClient;
    }

    public List<PagoResponse> listar() {
        return pagoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PagoResponse> listarPorUsuario(Long usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<PagoResponse> buscarPorId(Long id) {
        return pagoRepository.findById(id).map(this::toResponse);
    }

    @Transactional
    public PagoResponse crear(CrearPagoRequest request) {
        ResumenCarritoResponse resumen = obtenerResumenCarrito(request.usuarioId());
        validarCarrito(resumen);

        Pago pago = new Pago();
        pago.setUsuarioId(request.usuarioId());
        pago.setMonto(resumen.total());
        pago.setMetodoPago(request.metodoPago());
        pago.setEstado("APROBADO");
        pago.setCodigoTransaccion(generarCodigoTransaccion());

        Pago pagoGuardado = pagoRepository.save(pago);
        vaciarCarrito(request.usuarioId());

        return toResponse(pagoGuardado, resumen.nombreUsuario());
    }

    @Transactional
    public Optional<PagoResponse> actualizarEstado(Long id, ActualizarEstadoPagoRequest request) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    pago.setEstado(request.estado());
                    return pagoRepository.save(pago);
                })
                .map(this::toResponse);
    }

    @Transactional
    public boolean anular(Long id) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    pago.setEstado("ANULADO");
                    pagoRepository.save(pago);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!pagoRepository.existsById(id)) {
            return false;
        }

        pagoRepository.deleteById(id);
        return true;
    }

    private ResumenCarritoResponse obtenerResumenCarrito(Long usuarioId) {
        try {
            return carritoClient.obtenerResumen(usuarioId);
        } catch (FeignException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar el carrito");
        }
    }

    private void vaciarCarrito(Long usuarioId) {
        try {
            carritoClient.vaciarPorUsuario(usuarioId);
        } catch (FeignException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo vaciar el carrito");
        }
    }

    private void validarCarrito(ResumenCarritoResponse resumen) {
        if (resumen == null || resumen.total() == null || resumen.total() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El carrito no tiene productos para pagar");
        }
    }

    private String generarCodigoTransaccion() {
        String codigo;

        do {
            codigo = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (pagoRepository.existsByCodigoTransaccion(codigo));

        return codigo;
    }

    private PagoResponse toResponse(Pago pago) {
        return toResponse(pago, obtenerNombreUsuario(pago.getUsuarioId()));
    }

    private PagoResponse toResponse(Pago pago, String nombreUsuario) {
        return new PagoResponse(
                nombreUsuario,
                pago.getMonto(),
                pago.getMetodoPago(),
                pago.getEstado(),
                pago.getCodigoTransaccion(),
                pago.getFechaPago(),
                pago.getId(),
                pago.getUsuarioId()
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
