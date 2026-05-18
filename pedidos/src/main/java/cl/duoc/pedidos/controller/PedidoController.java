package cl.duoc.pedidos.controller;

import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Pedido> listar(){
        return pedidoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable Long id){
        return pedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody Pedido p){
        return new ResponseEntity<>(pedidoService.save(p), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable Long id, @Valid @RequestBody Pedido p){
        return pedidoService.update(id, p)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id){
        if (!pedidoService.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detalle")
    public ResponseEntity<?> detalle(){
        return ResponseEntity.ok(pedidoService.findPedidosConUsuario());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> pedidosPorUsuario(@PathVariable Long usuarioId){
        return ResponseEntity.ok(pedidoService.findByUsuario(usuarioId));
    }

    @GetMapping("/reportes/fecha")
    public ResponseEntity<?> pedidosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta){
        return ResponseEntity.ok(pedidoService.findByFechaPedidoBetween(desde, hasta));
    }

    @GetMapping("/reportes/precio")
    public ResponseEntity<?> pedidosPorPrecio(@RequestParam Double minimo, @RequestParam Double maximo){
        return ResponseEntity.ok(pedidoService.findByPrecioBetween(minimo, maximo));
    }
}
