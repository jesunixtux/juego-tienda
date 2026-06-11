package cl.duoc.carrito.controller;

import cl.duoc.carrito.dto.ActualizarCantidadRequest;
import cl.duoc.carrito.dto.AgregarItemCarritoRequest;
import cl.duoc.carrito.dto.ItemCarritoResponse;
import cl.duoc.carrito.dto.ResumenCarritoResponse;
import cl.duoc.carrito.service.CarritoService;
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
@RequestMapping("/carrito")
@Tag(name = "Carrito", description = "Items del carrito por usuario, resumen de compra y datos enriquecidos con usuario, videojuego y resenas.")
public class CarritoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarritoController.class);

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar carrito por usuario", description = "Muestra items con nombreUsuario, nombreVideojuego y resena del mismo usuario si existe.")
    public ResponseEntity<List<ItemCarritoResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        LOGGER.info("Listando carrito usuarioId={}", usuarioId);
        return ResponseEntity.ok(carritoService.listarPorUsuarioConVideojuego(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/resumen")
    @Operation(summary = "Resumen del carrito", description = "Calcula total e items enriquecidos para el usuario.")
    public ResponseEntity<ResumenCarritoResponse> obtenerResumen(@PathVariable Long usuarioId) {
        LOGGER.info("Obteniendo resumen carrito usuarioId={}", usuarioId);
        return ResponseEntity.ok(carritoService.obtenerResumen(usuarioId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item de carrito", description = "Obtiene un item especifico del carrito con datos del videojuego.")
    public ResponseEntity<ItemCarritoResponse> buscarPorId(@PathVariable Long id) {
        LOGGER.info("Buscando item carrito id={}", id);
        return carritoService.buscarPorIdConVideojuego(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Agregar item al carrito", description = "Agrega un videojuego al carrito o suma cantidad si ya existe.")
    public ResponseEntity<ItemCarritoResponse> agregar(@Valid @RequestBody AgregarItemCarritoRequest request) {
        LOGGER.info("Agregando item al carrito usuarioId={} videojuegoId={} cantidad={}",
                request.usuarioId(), request.videojuegoId(), request.cantidad());
        ItemCarritoResponse item = carritoService.agregarConVideojuego(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/{id}/cantidad")
    @Operation(summary = "Actualizar cantidad", description = "Cambia la cantidad de un item y recalcula subtotal.")
    public ResponseEntity<ItemCarritoResponse> actualizarCantidad(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarCantidadRequest request) {
        LOGGER.info("Actualizando cantidad item carrito id={} cantidad={}", id, request.cantidad());
        return carritoService.actualizarCantidadConVideojuego(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar item", description = "Elimina un item puntual del carrito.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        LOGGER.info("Eliminando item carrito id={}", id);
        if (!carritoService.eliminar(id)) {
            LOGGER.warn("No se encontro item carrito para eliminar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{usuarioId}")
    @Operation(summary = "Vaciar carrito por usuario", description = "Elimina todos los items del carrito del usuario.")
    public ResponseEntity<Void> vaciarPorUsuario(@PathVariable Long usuarioId) {
        LOGGER.info("Vaciando carrito usuarioId={}", usuarioId);
        carritoService.vaciarPorUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
