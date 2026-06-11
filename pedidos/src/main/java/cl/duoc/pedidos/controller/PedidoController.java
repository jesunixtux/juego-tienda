package cl.duoc.pedidos.controller;

import cl.duoc.pedidos.dto.PedidoDTO;
import cl.duoc.pedidos.model.Pedido;
import cl.duoc.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pedidos", description = "Pedidos y reportes por usuario, fecha o rango de precio.")
public class PedidoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PedidoController.class);

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Lista pedidos con nombreUsuario.")
    public ResponseEntity<List<PedidoDTO>> listar(){
        LOGGER.info("Listando pedidos");
        return ResponseEntity.ok(pedidoService.findAllConDetalle());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Obtiene un pedido especifico con datos del usuario.")
    public ResponseEntity<PedidoDTO> buscar(@PathVariable Long id){
        LOGGER.info("Buscando pedido id={}", id);
        return pedidoService.findByIdConDetalle(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear pedido", description = "Registra un pedido indicando usuario, juego, precio y fecha.")
    public ResponseEntity<PedidoDTO> crear(@Valid @RequestBody Pedido p){
        LOGGER.info("Creando pedido usuarioId={} nombreJuego={}", p.getUsuarioId(), p.getNombreJuego());
        return new ResponseEntity<>(pedidoService.saveConDetalle(p), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pedido", description = "Actualiza los datos de un pedido existente.")
    public ResponseEntity<PedidoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody Pedido p){
        LOGGER.info("Actualizando pedido id={}", id);
        return pedidoService.updateConDetalle(id, p)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pedido", description = "Elimina un pedido por ID.")
    public ResponseEntity<Void> borrar(@PathVariable Long id){
        LOGGER.info("Eliminando pedido id={}", id);
        if (!pedidoService.delete(id)) {
            LOGGER.warn("No se encontro pedido para eliminar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detalle")
    @Operation(summary = "Detalle de pedidos", description = "Lista pedidos con informacion de usuario.")
    public ResponseEntity<List<PedidoDTO>> detalle(){
        LOGGER.info("Listando detalle de pedidos");
        return ResponseEntity.ok(pedidoService.findPedidosConUsuario());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Pedidos por usuario", description = "Lista pedidos asociados a un usuario.")
    public ResponseEntity<List<PedidoDTO>> pedidosPorUsuario(@PathVariable Long usuarioId){
        LOGGER.info("Listando pedidos usuarioId={}", usuarioId);
        return ResponseEntity.ok(pedidoService.findByUsuarioConDetalle(usuarioId));
    }

    @GetMapping("/reportes/fecha")
    @Operation(summary = "Reporte por fecha", description = "Filtra pedidos entre fechas usando parametros desde y hasta en formato ISO yyyy-MM-dd.")
    public ResponseEntity<List<PedidoDTO>> pedidosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta){
        LOGGER.info("Listando pedidos por fecha desde={} hasta={}", desde, hasta);
        return ResponseEntity.ok(pedidoService.findByFechaPedidoBetweenConDetalle(desde, hasta));
    }

    @GetMapping("/reportes/precio")
    @Operation(summary = "Reporte por precio", description = "Filtra pedidos entre minimo y maximo.")
    public ResponseEntity<List<PedidoDTO>> pedidosPorPrecio(@RequestParam Double minimo, @RequestParam Double maximo){
        LOGGER.info("Listando pedidos por precio minimo={} maximo={}", minimo, maximo);
        return ResponseEntity.ok(pedidoService.findByPrecioBetweenConDetalle(minimo, maximo));
    }
}
