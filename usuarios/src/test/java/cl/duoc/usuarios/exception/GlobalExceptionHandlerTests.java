package cl.duoc.usuarios.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final HttpServletRequest request = request("/usuarios");

    @Test
    void handleApiRespetaStatusDeLaExcepcion() {
        ResponseEntity<ErrorResponse> response = handler.handleApi(new ConflictException("Correo duplicado"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("Correo duplicado");
        assertThat(response.getBody().path()).isEqualTo("/usuarios");
    }

    @Test
    void handleResponseStatusUsaReasonOReasonPhrase() {
        ResponseEntity<ErrorResponse> withReason = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontrado"),
                request);
        ResponseEntity<ErrorResponse> withoutReason = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.BAD_REQUEST),
                request);

        assertThat(withReason.getBody().message()).isEqualTo("No encontrado");
        assertThat(withoutReason.getBody().message()).isEqualTo("Bad Request");
    }

    @Test
    void handleGenericDevuelveErrorInternoControlado() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new RuntimeException("boom"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Error interno del servidor");
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }
}
