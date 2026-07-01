package cl.duoc.inventario.exception;

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
    private final HttpServletRequest request = request("/inventario");

    @Test
    void handleApiCubreConflictoNoEncontradoYServicioExterno() {
        ResponseEntity<ErrorResponse> conflict = handler.handleApi(new ConflictException("Stock insuficiente"), request);
        ResponseEntity<ErrorResponse> notFound = handler.handleApi(new ResourceNotFoundException("Videojuego no encontrado"), request);
        ResponseEntity<ErrorResponse> badGateway = handler.handleApi(new ExternalServiceException("Videojuegos no responde"), request);

        assertThat(conflict.getStatusCode().value()).isEqualTo(409);
        assertThat(notFound.getStatusCode().value()).isEqualTo(404);
        assertThat(badGateway.getStatusCode().value()).isEqualTo(502);
    }

    @Test
    void handleResponseStatusYGenericDevuelvenCuerpoEstandar() {
        ResponseEntity<ErrorResponse> responseStatus = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado"),
                request);
        ResponseEntity<ErrorResponse> generic = handler.handleGeneric(new RuntimeException("boom"), request);

        assertThat(responseStatus.getBody().message()).isEqualTo("Inventario no encontrado");
        assertThat(generic.getStatusCode().value()).isEqualTo(500);
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }
}
