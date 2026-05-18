package cl.duoc.pagos.service;

import cl.duoc.pagos.client.CarritoClient;
import cl.duoc.pagos.dto.ActualizarEstadoPagoRequest;
import cl.duoc.pagos.dto.CrearPagoRequest;
import cl.duoc.pagos.dto.ResumenCarritoResponse;
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

    public PagoService(PagoRepository pagoRepository, CarritoClient carritoClient) {
        this.pagoRepository = pagoRepository;
        this.carritoClient = carritoClient;
    }

    public List<Pago> listar() {
        return pagoRepository.findAll();
    }

    public List<Pago> listarPorUsuario(Long usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Pago> buscarPorId(Long id) {
        return pagoRepository.findById(id);
    }

    @Transactional
    public Pago crear(CrearPagoRequest request) {
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

        return pagoGuardado;
    }

    @Transactional
    public Optional<Pago> actualizarEstado(Long id, ActualizarEstadoPagoRequest request) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    pago.setEstado(request.estado());
                    return pagoRepository.save(pago);
                });
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
}
