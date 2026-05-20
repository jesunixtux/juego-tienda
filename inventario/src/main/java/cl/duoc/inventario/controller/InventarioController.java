package cl.duoc.inventario.controller;

import cl.duoc.inventario.dto.ActualizarStockRequest;
import cl.duoc.inventario.dto.CrearInventarioRequest;
import cl.duoc.inventario.dto.InventarioResponse;
import cl.duoc.inventario.dto.MovimientoStockRequest;
import cl.duoc.inventario.service.InventarioService;
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
@RequestMapping("/inventario")
public class InventarioController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InventarioController.class);

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public ResponseEntity<List<InventarioResponse>> listar() {
        LOGGER.info("Listando inventario");
        return ResponseEntity.ok(inventarioService.listarConVideojuego());
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<InventarioResponse>> listarBajoStock() {
        LOGGER.info("Listando inventario bajo stock");
        return ResponseEntity.ok(inventarioService.listarBajoStockConVideojuego());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponse> buscarPorId(@PathVariable Long id) {
        LOGGER.info("Buscando inventario id={}", id);
        return inventarioService.buscarPorIdConVideojuego(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<InventarioResponse> buscarPorVideojuego(@PathVariable Long videojuegoId) {
        LOGGER.info("Buscando inventario videojuegoId={}", videojuegoId);
        return inventarioService.buscarPorVideojuegoConDetalle(videojuegoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InventarioResponse> crear(@Valid @RequestBody CrearInventarioRequest request) {
        LOGGER.info("Creando inventario videojuegoId={} stock={}", request.videojuegoId(), request.stock());
        InventarioResponse inventario = inventarioService.crearConDetalle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CrearInventarioRequest request) {
        LOGGER.info("Actualizando inventario id={}", id);
        return inventarioService.actualizarConDetalle(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/videojuego/{videojuegoId}/stock")
    public ResponseEntity<InventarioResponse> actualizarStockPorVideojuego(
            @PathVariable Long videojuegoId,
            @Valid @RequestBody ActualizarStockRequest request) {
        LOGGER.info("Actualizando stock videojuegoId={} stock={}", videojuegoId, request.stock());
        return inventarioService.actualizarStockPorVideojuegoConDetalle(videojuegoId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/videojuego/{videojuegoId}/entrada")
    public ResponseEntity<InventarioResponse> aumentarStock(
            @PathVariable Long videojuegoId,
            @Valid @RequestBody MovimientoStockRequest request) {
        LOGGER.info("Aumentando stock videojuegoId={} cantidad={}", videojuegoId, request.cantidad());
        return inventarioService.aumentarStockConDetalle(videojuegoId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/videojuego/{videojuegoId}/salida")
    public ResponseEntity<InventarioResponse> disminuirStock(
            @PathVariable Long videojuegoId,
            @Valid @RequestBody MovimientoStockRequest request) {
        LOGGER.info("Disminuyendo stock videojuegoId={} cantidad={}", videojuegoId, request.cantidad());
        return inventarioService.disminuirStockConDetalle(videojuegoId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        LOGGER.info("Eliminando inventario id={}", id);
        if (!inventarioService.eliminar(id)) {
            LOGGER.warn("No se encontro inventario para eliminar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
