package cl.duoc.authentication.controller;

import cl.duoc.authentication.dto.AuthResponse;
import cl.duoc.authentication.dto.CambiarPasswordRequest;
import cl.duoc.authentication.dto.CredencialResponse;
import cl.duoc.authentication.dto.LoginRequest;
import cl.duoc.authentication.dto.RegistroRequest;
import cl.duoc.authentication.service.AuthenticationService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationControllerUnitTests {

    private final AuthenticationService authenticationService = mock(AuthenticationService.class);
    private final AuthenticationController controller = new AuthenticationController(authenticationService);

    @Test
    void registrarYLoginRetornanRespuestasCorrectas() {
        RegistroRequest registro = new RegistroRequest("Jesus", "Emilio", "jesus@tienda.cl", null, null, "CLIENTE", "clave123");
        LoginRequest login = new LoginRequest("jesus@tienda.cl", "clave123");
        AuthResponse response = authResponse("Registro exitoso");

        when(authenticationService.registrar(registro)).thenReturn(response);
        when(authenticationService.login(login)).thenReturn(authResponse("Login exitoso"));

        assertThat(controller.registrar(registro).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.login(login).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.login(login).getBody().mensaje()).isEqualTo("Login exitoso");
    }

    @Test
    void credencialesCubrenListaBusquedaCambioYDesactivacion() {
        CredencialResponse credencial = new CredencialResponse(1L, 2L, "jesus@tienda.cl", true, null, null);
        CambiarPasswordRequest cambio = new CambiarPasswordRequest("clave123", "nueva123");
        when(authenticationService.listarCredenciales()).thenReturn(List.of(credencial));
        when(authenticationService.buscarCredencial(1L)).thenReturn(Optional.of(credencial));
        when(authenticationService.buscarCredencial(99L)).thenReturn(Optional.empty());
        when(authenticationService.cambiarPassword(1L, cambio)).thenReturn(Optional.of(credencial));
        when(authenticationService.cambiarPassword(99L, cambio)).thenReturn(Optional.empty());
        when(authenticationService.desactivar(1L)).thenReturn(true);
        when(authenticationService.desactivar(99L)).thenReturn(false);

        assertThat(controller.listarCredenciales().getBody()).containsExactly(credencial);
        assertThat(controller.buscarCredencial(1L).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.buscarCredencial(99L).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.cambiarPassword(1L, cambio).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.cambiarPassword(99L, cambio).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.desactivar(1L).getStatusCode().value()).isEqualTo(204);
        assertThat(controller.desactivar(99L).getStatusCode().value()).isEqualTo(404);
    }

    private AuthResponse authResponse(String mensaje) {
        return new AuthResponse("Jesus Emilio", "jesus@tienda.cl", "CLIENTE", mensaje, true, 2L);
    }
}
