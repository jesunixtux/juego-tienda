package cl.duoc.usuarios.controller;

import cl.duoc.usuarios.model.Usuario;
import cl.duoc.usuarios.service.UsuarioService;
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
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Administracion de usuarios, roles, datos de contacto y busqueda por correo.")
public class UsuarioController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Lista todos los usuarios o solo activos usando el parametro activos=true.")
    public ResponseEntity<List<Usuario>> listar(@RequestParam(required = false) Boolean activos) {
        LOGGER.info("Listando usuarios activos={}", activos);
        if (Boolean.TRUE.equals(activos)) {
            return ResponseEntity.ok(usuarioService.listarActivos());
        }

        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Obtiene un usuario especifico por identificador.")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        LOGGER.info("Buscando usuario id={}", id);
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar usuario por correo", description = "Obtiene un usuario usando el correo exacto.")
    public ResponseEntity<Usuario> buscarPorCorreo(@RequestParam String correo) {
        LOGGER.info("Buscando usuario correo={}", correo);
        return usuarioService.buscarPorCorreo(correo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un usuario nuevo validando correo unico.")
    public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
        LOGGER.info("Creando usuario correo={}", usuario.getCorreo());
        Usuario nuevoUsuario = usuarioService.crear(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza datos personales, rol y estado activo.")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        LOGGER.info("Actualizando usuario id={}", id);
        return usuarioService.actualizar(id, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar usuario", description = "No borra fisicamente: marca el usuario como inactivo.")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        LOGGER.info("Desactivando usuario id={}", id);
        if (!usuarioService.desactivar(id)) {
            LOGGER.warn("No se encontro usuario para desactivar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
