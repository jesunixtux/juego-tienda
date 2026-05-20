package cl.duoc.pedidos.controller;

import cl.duoc.pedidos.dto.PedidoDTO;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PedidoController.class);

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listar(){
        LOGGER.info("Listando pedidos");
        return ResponseEntity.ok(pedidoService.findAllConDetalle());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscar(@PathVariable Long id){
        LOGGER.info("Buscando pedido id={}", id);
        return pedidoService.findByIdConDetalle(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> crear(@Valid @RequestBody Pedido p){
        LOGGER.info("Creando pedido usuarioId={} nombreJuego={}", p.getUsuarioId(), p.getNombreJuego());
        return new ResponseEntity<>(pedidoService.saveConDetalle(p), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody Pedido p){
        LOGGER.info("Actualizando pedido id={}", id);
        return pedidoService.updateConDetalle(id, p)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id){
        LOGGER.info("Eliminando pedido id={}", id);
        if (!pedidoService.delete(id)) {
            LOGGER.warn("No se encontro pedido para eliminar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detalle")
    public ResponseEntity<List<PedidoDTO>> detalle(){
        LOGGER.info("Listando detalle de pedidos");
        return ResponseEntity.ok(pedidoService.findPedidosConUsuario());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoDTO>> pedidosPorUsuario(@PathVariable Long usuarioId){
        LOGGER.info("Listando pedidos usuarioId={}", usuarioId);
        return ResponseEntity.ok(pedidoService.findByUsuarioConDetalle(usuarioId));
    }

    @GetMapping("/reportes/fecha")
    public ResponseEntity<List<PedidoDTO>> pedidosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta){
        LOGGER.info("Listando pedidos por fecha desde={} hasta={}", desde, hasta);
        return ResponseEntity.ok(pedidoService.findByFechaPedidoBetweenConDetalle(desde, hasta));
    }

    @GetMapping("/reportes/precio")
    public ResponseEntity<List<PedidoDTO>> pedidosPorPrecio(@RequestParam Double minimo, @RequestParam Double maximo){
        LOGGER.info("Listando pedidos por precio minimo={} maximo={}", minimo, maximo);
        return ResponseEntity.ok(pedidoService.findByPrecioBetweenConDetalle(minimo, maximo));
    }
}
