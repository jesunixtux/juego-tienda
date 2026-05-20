package cl.duoc.authentication.controller;

import cl.duoc.authentication.dto.AuthResponse;
import cl.duoc.authentication.dto.CambiarPasswordRequest;
import cl.duoc.authentication.dto.LoginRequest;
import cl.duoc.authentication.dto.RegistroRequest;
import cl.duoc.authentication.model.Credencial;
import cl.duoc.authentication.service.AuthenticationService;
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
public class AuthenticationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        LOGGER.info("Registrando credencial correo={}", request.correo());
        AuthResponse response = authenticationService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LOGGER.info("Intento de login correo={}", request.correo());
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @GetMapping("/credenciales")
    public ResponseEntity<List<Credencial>> listarCredenciales() {
        LOGGER.info("Listando credenciales");
        return ResponseEntity.ok(authenticationService.listarCredenciales());
    }

    @GetMapping("/credenciales/{id}")
    public ResponseEntity<Credencial> buscarCredencial(@PathVariable Long id) {
        LOGGER.info("Buscando credencial id={}", id);
        return authenticationService.buscarCredencial(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/credenciales/{id}/password")
    public ResponseEntity<Credencial> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordRequest request) {
        LOGGER.info("Cambiando password credencial id={}", id);
        return authenticationService.cambiarPassword(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/credenciales/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        LOGGER.info("Desactivando credencial id={}", id);
        if (!authenticationService.desactivar(id)) {
            LOGGER.warn("No se encontro credencial para desactivar id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
