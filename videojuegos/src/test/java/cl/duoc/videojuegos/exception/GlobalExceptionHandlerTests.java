package cl.duoc.videojuegos.exception;

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
    private final HttpServletRequest request = request("/videojuegos");

    @Test
    void handlePlataformaDevuelveBadRequestPersonalizado() {
        ResponseEntity<ErrorResponse> response = handler.handlePlataforma(
                new PlataformaException("Plataforma no valida"),
                request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Plataforma no valida");
        assertThat(response.getBody().path()).isEqualTo("/videojuegos");
    }

    @Test
    void handleResponseStatusUsaReasonCuandoExiste() {
        ResponseEntity<ErrorResponse> response = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontrado"),
                request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("No encontrado");
    }

    @Test
    void handleGenericDevuelveErrorInternoControlado() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new RuntimeException("boom"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Error interno del servidor");
    }

    @Test
    void plataformaExeptionDeprecatedMantieneCompatibilidad() {
        PlataformaExeption exception = new PlataformaExeption("Mensaje legacy");

        assertThat(exception).isInstanceOf(PlataformaException.class);
        assertThat(exception.getMessage()).isEqualTo("Mensaje legacy");
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }
}
