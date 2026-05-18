package cl.duoc.inventario.controller;

import cl.duoc.inventario.dto.ActualizarStockRequest;
import cl.duoc.inventario.dto.CrearInventarioRequest;
import cl.duoc.inventario.dto.MovimientoStockRequest;
import cl.duoc.inventario.model.Inventario;
import cl.duoc.inventario.service.InventarioService;
import jakarta.validation.Valid;
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
    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public List<Inventario> listar() {
        return inventarioService.listar();
    }

    @GetMapping("/bajo-stock")
    public List<Inventario> listarBajoStock() {
        return inventarioService.listarBajoStock();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> buscarPorId(@PathVariable Long id) {
        return inventarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<Inventario> buscarPorVideojuego(@PathVariable Long videojuegoId) {
        return inventarioService.buscarPorVideojuego(videojuegoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Inventario> crear(@Valid @RequestBody CrearInventarioRequest request) {
        Inventario inventario = inventarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CrearInventarioRequest request) {
        return inventarioService.actualizar(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/videojuego/{videojuegoId}/stock")
    public ResponseEntity<Inventario> actualizarStockPorVideojuego(
            @PathVariable Long videojuegoId,
            @Valid @RequestBody ActualizarStockRequest request) {
        return inventarioService.actualizarStockPorVideojuego(videojuegoId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/videojuego/{videojuegoId}/entrada")
    public ResponseEntity<Inventario> aumentarStock(
            @PathVariable Long videojuegoId,
            @Valid @RequestBody MovimientoStockRequest request) {
        return inventarioService.aumentarStock(videojuegoId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/videojuego/{videojuegoId}/salida")
    public ResponseEntity<Inventario> disminuirStock(
            @PathVariable Long videojuegoId,
            @Valid @RequestBody MovimientoStockRequest request) {
        return inventarioService.disminuirStock(videojuegoId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!inventarioService.eliminar(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
