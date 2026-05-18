package cl.duoc.carrito.controller;

import cl.duoc.carrito.dto.ActualizarCantidadRequest;
import cl.duoc.carrito.dto.AgregarItemCarritoRequest;
import cl.duoc.carrito.dto.ResumenCarritoResponse;
import cl.duoc.carrito.model.ItemCarrito;
import cl.duoc.carrito.service.CarritoService;
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
@RequestMapping("/carrito")
public class CarritoController {
    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ItemCarrito> listarPorUsuario(@PathVariable Long usuarioId) {
        return carritoService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}/resumen")
    public ResumenCarritoResponse obtenerResumen(@PathVariable Long usuarioId) {
        return carritoService.obtenerResumen(usuarioId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemCarrito> buscarPorId(@PathVariable Long id) {
        return carritoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ItemCarrito> agregar(@Valid @RequestBody AgregarItemCarritoRequest request) {
        ItemCarrito item = carritoService.agregar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/{id}/cantidad")
    public ResponseEntity<ItemCarrito> actualizarCantidad(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarCantidadRequest request) {
        return carritoService.actualizarCantidad(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!carritoService.eliminar(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<Void> vaciarPorUsuario(@PathVariable Long usuarioId) {
        carritoService.vaciarPorUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
