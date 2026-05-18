package cl.duoc.resenas.controller;

import cl.duoc.resenas.model.Resena;
import cl.duoc.resenas.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/resenas")
public class ResenaController {
    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping
    public List<Resena> listar() {
        return resenaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resena> buscar(@PathVariable Long id) {
        return resenaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Resena> crear(@Valid @RequestBody Resena r) {
        return new ResponseEntity<>(resenaService.save(r), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resena> actualizar(@PathVariable Long id, @Valid @RequestBody Resena r) {
        return resenaService.update(id, r)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (!resenaService.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detalle")
    public ResponseEntity<?> detalle() {
        return ResponseEntity.ok(resenaService.findConUsuario());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.findByUsuario(usuarioId));
    }

    @GetMapping("/reportes/fecha")
    public ResponseEntity<?> porFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        return ResponseEntity.ok(resenaService.findByFecha(desde, hasta));
    }

    @GetMapping("/reportes/puntuacion")
    public ResponseEntity<?> porPuntuacion(
            @RequestParam Integer min,
            @RequestParam Integer max) {

        return ResponseEntity.ok(resenaService.findByPuntuacion(min, max));
    }
}
