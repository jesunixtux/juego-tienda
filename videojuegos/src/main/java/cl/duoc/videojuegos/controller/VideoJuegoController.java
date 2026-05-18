package cl.duoc.videojuegos.controller;

import cl.duoc.videojuegos.model.VideoJuego;
import cl.duoc.videojuegos.service.VideoJuegoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/videojuegos" )
public class VideoJuegoController {
    private final VideoJuegoService videoJuegoService;

    public VideoJuegoController(VideoJuegoService videoJuegoService) {
        this.videoJuegoService = videoJuegoService;
    }

    @GetMapping
    public List<VideoJuego> listar() {
        return videoJuegoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoJuego> buscarPorId(@PathVariable Long id) {
        return videoJuegoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VideoJuego> crear(@Valid @RequestBody VideoJuego videoJuego) {
        VideoJuego nuevoVideoJuego = videoJuegoService.crear(videoJuego);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoVideoJuego);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoJuego> actualizar(@PathVariable Long id, @Valid @RequestBody VideoJuego videoJuego) {
        return videoJuegoService.actualizar(id, videoJuego)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!videoJuegoService.eliminar(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public List<VideoJuego> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String plataforma) {
        if (nombre != null && !nombre.isBlank()) {
            return videoJuegoService.buscarPorNombre(nombre);
        }

        if (categoria != null && !categoria.isBlank()) {
            return videoJuegoService.buscarPorCategoria(categoria);
        }

        if (plataforma != null && !plataforma.isBlank()) {
            return videoJuegoService.buscarPorPlataforma(plataforma);
        }

        return videoJuegoService.listar();
    }

}
