package cl.duoc.videojuegos.controller;

import cl.duoc.videojuegos.model.VideoJuego;
import cl.duoc.videojuegos.service.VideoJuegoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/videojuegos" )
@Tag(name = "Videojuegos", description = "Catalogo de videojuegos y busquedas por precio, plataforma, categoria o nombre.")
public class VideoJuegoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoJuegoController.class);

    private final VideoJuegoService videoJuegoService;

    public VideoJuegoController(VideoJuegoService videoJuegoService) {
        this.videoJuegoService = videoJuegoService;
    }

    @GetMapping
    @Operation(summary = "Listar videojuegos", description = "Devuelve todo el catalogo disponible.")
    public ResponseEntity<List<VideoJuego>> listar() {
        LOGGER.info("Listando videojuegos");
        return ResponseEntity.ok(videoJuegoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar videojuego por ID", description = "Consulta un videojuego especifico por su identificador.")
    public ResponseEntity<VideoJuego> buscarPorId(@PathVariable Long id) {
        LOGGER.info("Buscando videojuego id={}", id);
        return videoJuegoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear videojuego", description = "Crea un videojuego validando datos obligatorios y plataforma permitida.")
    public ResponseEntity<VideoJuego> crear(@Valid @RequestBody VideoJuego videoJuego) {
        LOGGER.info("Creando videojuego nombre={}", videoJuego.getNombre());
        VideoJuego nuevoVideoJuego = videoJuegoService.crear(videoJuego);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoVideoJuego);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar videojuego", description = "Actualiza los datos de un videojuego existente.")
    public ResponseEntity<VideoJuego> actualizar(@PathVariable Long id, @Valid @RequestBody VideoJuego videoJuego) {
        LOGGER.info("Actualizando videojuego id={}", id);
        return videoJuegoService.actualizar(id, videoJuego)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar videojuego", description = "Elimina un videojuego por ID.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        LOGGER.info("Eliminando videojuego id={}", id);
        if (!videoJuegoService.eliminar(id)) {
            LOGGER.warn("No se encontro videojuego para eliminar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(
            summary = "Buscar videojuegos",
            description = "Filtra por nombre, categoria, plataforma o rango de precio con precioMin/precioMax.")
    public ResponseEntity<List<VideoJuego>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String plataforma,
            @RequestParam(required = false) Integer precioMin,
            @RequestParam(required = false) Integer precioMax) {
        LOGGER.info(
                "Buscando videojuegos nombre={} categoria={} plataforma={} precioMin={} precioMax={}",
                nombre,
                categoria,
                plataforma,
                precioMin,
                precioMax);
        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(videoJuegoService.buscarPorNombre(nombre));
        }

        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(videoJuegoService.buscarPorCategoria(categoria));
        }

        if (plataforma != null && !plataforma.isBlank()) {
            return ResponseEntity.ok(videoJuegoService.buscarPorPlataforma(plataforma));
        }

        if (precioMin != null || precioMax != null) {
            return ResponseEntity.ok(videoJuegoService.buscarPorPrecio(precioMin, precioMax));
        }

        return ResponseEntity.ok(videoJuegoService.listar());
    }

}
