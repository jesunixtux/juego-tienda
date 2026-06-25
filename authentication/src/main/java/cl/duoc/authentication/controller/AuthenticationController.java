package cl.duoc.authentication.controller;

import cl.duoc.authentication.dto.AuthResponse;
import cl.duoc.authentication.dto.CambiarPasswordRequest;
import cl.duoc.authentication.dto.CredencialResponse;
import cl.duoc.authentication.dto.LoginRequest;
import cl.duoc.authentication.dto.RegistroRequest;
import cl.duoc.authentication.service.AuthenticationService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Registro, login, emision de token informativo y administracion de credenciales.")
public class AuthenticationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar usuario", description = "Crea usuario y credencial, y devuelve datos de autenticacion. El gateway no bloquea rutas por token.")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        LOGGER.info("Registrando credencial correo={}", request.correo());
        AuthResponse response = authenticationService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion", description = "Valida correo y password hasheada, y devuelve un token informativo para pruebas.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LOGGER.info("Intento de login correo={}", request.correo());
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @GetMapping("/credenciales")
    @Operation(summary = "Listar credenciales", description = "Lista credenciales sin exponer passwordHash.")
    public ResponseEntity<List<CredencialResponse>> listarCredenciales() {
        LOGGER.info("Listando credenciales");
        return ResponseEntity.ok(authenticationService.listarCredenciales());
    }

    @GetMapping("/credenciales/{id}")
    @Operation(summary = "Buscar credencial", description = "Obtiene una credencial por ID sin exponer passwordHash.")
    public ResponseEntity<CredencialResponse> buscarCredencial(@PathVariable Long id) {
        LOGGER.info("Buscando credencial id={}", id);
        return authenticationService.buscarCredencial(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/credenciales/{id}/password")
    @Operation(summary = "Cambiar password", description = "Valida la password actual y guarda la nueva usando hash SHA-256 con sal.")
    public ResponseEntity<CredencialResponse> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordRequest request) {
        LOGGER.info("Cambiando password credencial id={}", id);
        return authenticationService.cambiarPassword(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/credenciales/{id}")
    @Operation(summary = "Desactivar credencial", description = "Marca una credencial como inactiva para bloquear futuros login.")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        LOGGER.info("Desactivando credencial id={}", id);
        if (!authenticationService.desactivar(id)) {
            LOGGER.warn("No se encontro credencial para desactivar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
