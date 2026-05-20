package cl.duoc.resenas.controller;

import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.model.Resena;
import cl.duoc.resenas.service.ResenaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/resenas")
public class ResenaController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResenaController.class);

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping
    public ResponseEntity<List<ResenaDTO>> listar() {
        LOGGER.info("Listando resenas");
        return ResponseEntity.ok(resenaService.findAllConUsuario());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO> buscar(@PathVariable Long id) {
        LOGGER.info("Buscando resena id={}", id);
        return resenaService.findByIdConUsuario(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ResenaDTO> crear(@Valid @RequestBody Resena r) {
        LOGGER.info("Creando resena usuarioId={} nombreJuego={}", r.getUsuarioId(), r.getNombreJuego());
        return new ResponseEntity<>(resenaService.saveConUsuario(r), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResenaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody Resena r) {
        LOGGER.info("Actualizando resena id={}", id);
        return resenaService.updateConUsuario(id, r)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        LOGGER.info("Eliminando resena id={}", id);
        if (!resenaService.delete(id)) {
            LOGGER.warn("No se encontro resena para eliminar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detalle")
    public ResponseEntity<List<ResenaDTO>> detalle() {
        LOGGER.info("Listando detalle de resenas");
        return ResponseEntity.ok(resenaService.findConUsuario());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaDTO>> porUsuario(@PathVariable Long usuarioId) {
        LOGGER.info("Listando resenas usuarioId={}", usuarioId);
        return ResponseEntity.ok(resenaService.findByUsuarioConDetalle(usuarioId));
    }

    @GetMapping("/reportes/fecha")
    public ResponseEntity<List<ResenaDTO>> porFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        LOGGER.info("Listando resenas por fecha desde={} hasta={}", desde, hasta);
        return ResponseEntity.ok(resenaService.findByFechaConDetalle(desde, hasta));
    }

    @GetMapping("/reportes/puntuacion")
    public ResponseEntity<List<ResenaDTO>> porPuntuacion(
            @RequestParam Integer min,
            @RequestParam Integer max) {

        LOGGER.info("Listando resenas por puntuacion min={} max={}", min, max);
        return ResponseEntity.ok(resenaService.findByPuntuacionConDetalle(min, max));
    }
}
