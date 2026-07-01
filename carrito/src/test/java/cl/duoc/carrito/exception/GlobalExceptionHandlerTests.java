package cl.duoc.carrito.exception;

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
    private final HttpServletRequest request = request("/carrito");

    @Test
    void handleApiCubreNoEncontradoYServicioExterno() {
        ResponseEntity<ErrorResponse> notFound = handler.handleApi(new ResourceNotFoundException("Videojuego no encontrado"), request);
        ResponseEntity<ErrorResponse> badGateway = handler.handleApi(new ExternalServiceException("Microservicio caido"), request);

        assertThat(notFound.getStatusCode().value()).isEqualTo(404);
        assertThat(badGateway.getStatusCode().value()).isEqualTo(502);
        assertThat(badGateway.getBody().message()).isEqualTo("Microservicio caido");
    }

    @Test
    void handleResponseStatusYGenericDevuelvenCuerpoEstandar() {
        ResponseEntity<ErrorResponse> badRequest = handler.handleResponseStatus(new ResponseStatusException(HttpStatus.BAD_REQUEST), request);
        ResponseEntity<ErrorResponse> generic = handler.handleGeneric(new RuntimeException("boom"), request);

        assertThat(badRequest.getBody().message()).isEqualTo("Bad Request");
        assertThat(generic.getStatusCode().value()).isEqualTo(500);
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }
}
