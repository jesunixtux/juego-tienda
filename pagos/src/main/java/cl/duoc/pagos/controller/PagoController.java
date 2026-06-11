package cl.duoc.pagos.controller;

import cl.duoc.pagos.dto.ActualizarEstadoPagoRequest;
import cl.duoc.pagos.dto.CrearPagoRequest;
import cl.duoc.pagos.dto.PagoResponse;
import cl.duoc.pagos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pagos")
@Tag(name = "Pagos", description = "Pagos generados desde el carrito, estados de pago y anulaciones.")
public class PagoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PagoController.class);

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    @Operation(summary = "Listar pagos", description = "Lista pagos con nombreUsuario y codigo de transaccion.")
    public ResponseEntity<List<PagoResponse>> listar() {
        LOGGER.info("Listando pagos");
        return ResponseEntity.ok(pagoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pago por ID", description = "Obtiene un pago especifico.")
    public ResponseEntity<PagoResponse> buscarPorId(@PathVariable Long id) {
        LOGGER.info("Buscando pago id={}", id);
        return pagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar pagos por usuario", description = "Filtra pagos por usuarioId.")
    public ResponseEntity<List<PagoResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        LOGGER.info("Listando pagos usuarioId={}", usuarioId);
        return ResponseEntity.ok(pagoService.listarPorUsuario(usuarioId));
    }

    @PostMapping
    @Operation(summary = "Crear pago", description = "Consulta el resumen del carrito, crea el pago aprobado y vacia el carrito.")
    public ResponseEntity<PagoResponse> crear(@Valid @RequestBody CrearPagoRequest request) {
        LOGGER.info("Creando pago usuarioId={} metodo={}", request.usuarioId(), request.metodoPago());
        PagoResponse pago = pagoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de pago", description = "Cambia manualmente el estado de una transaccion.")
    public ResponseEntity<PagoResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoPagoRequest request) {
        LOGGER.info("Actualizando estado pago id={} estado={}", id, request.estado());
        return pagoService.actualizarEstado(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/anular")
    @Operation(summary = "Anular pago", description = "Marca el pago como ANULADO.")
    public ResponseEntity<Void> anular(@PathVariable Long id) {
        LOGGER.info("Anulando pago id={}", id);
        if (!pagoService.anular(id)) {
            LOGGER.warn("No se encontro pago para anular id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pago", description = "Elimina un pago por ID.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        LOGGER.info("Eliminando pago id={}", id);
        if (!pagoService.eliminar(id)) {
            LOGGER.warn("No se encontro pago para eliminar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
