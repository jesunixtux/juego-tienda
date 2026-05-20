package cl.duoc.carrito.controller;

import cl.duoc.carrito.dto.ActualizarCantidadRequest;
import cl.duoc.carrito.dto.AgregarItemCarritoRequest;
import cl.duoc.carrito.dto.ItemCarritoResponse;
import cl.duoc.carrito.dto.ResumenCarritoResponse;
import cl.duoc.carrito.service.CarritoService;
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
@RequestMapping("/carrito")
public class CarritoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarritoController.class);

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ItemCarritoResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        LOGGER.info("Listando carrito usuarioId={}", usuarioId);
        return ResponseEntity.ok(carritoService.listarPorUsuarioConVideojuego(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/resumen")
    public ResponseEntity<ResumenCarritoResponse> obtenerResumen(@PathVariable Long usuarioId) {
        LOGGER.info("Obteniendo resumen carrito usuarioId={}", usuarioId);
        return ResponseEntity.ok(carritoService.obtenerResumen(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemCarritoResponse> buscarPorId(@PathVariable Long id) {
        LOGGER.info("Buscando item carrito id={}", id);
        return carritoService.buscarPorIdConVideojuego(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ItemCarritoResponse> agregar(@Valid @RequestBody AgregarItemCarritoRequest request) {
        LOGGER.info("Agregando item al carrito usuarioId={} videojuegoId={} cantidad={}",
                request.usuarioId(), request.videojuegoId(), request.cantidad());
        ItemCarritoResponse item = carritoService.agregarConVideojuego(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/{id}/cantidad")
    public ResponseEntity<ItemCarritoResponse> actualizarCantidad(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarCantidadRequest request) {
        LOGGER.info("Actualizando cantidad item carrito id={} cantidad={}", id, request.cantidad());
        return carritoService.actualizarCantidadConVideojuego(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        LOGGER.info("Eliminando item carrito id={}", id);
        if (!carritoService.eliminar(id)) {
            LOGGER.warn("No se encontro item carrito para eliminar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<Void> vaciarPorUsuario(@PathVariable Long usuarioId) {
        LOGGER.info("Vaciando carrito usuarioId={}", usuarioId);
        carritoService.vaciarPorUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
