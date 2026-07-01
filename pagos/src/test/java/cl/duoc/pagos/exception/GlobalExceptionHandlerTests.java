package cl.duoc.pagos.exception;

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
    private final HttpServletRequest request = request("/pagos");

    @Test
    void handleApiCubreConflictoYServicioExterno() {
        ResponseEntity<ErrorResponse> conflict = handler.handleApi(new ConflictException("Carrito vacio"), request);
        ResponseEntity<ErrorResponse> badGateway = handler.handleApi(new ExternalServiceException("Carrito no responde"), request);

        assertThat(conflict.getStatusCode().value()).isEqualTo(409);
        assertThat(conflict.getBody().message()).isEqualTo("Carrito vacio");
        assertThat(badGateway.getStatusCode().value()).isEqualTo(502);
    }

    @Test
    void handleResponseStatusYGenericDevuelvenCuerpoEstandar() {
        ResponseEntity<ErrorResponse> responseStatus = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"),
                request);
        ResponseEntity<ErrorResponse> generic = handler.handleGeneric(new RuntimeException("boom"), request);

        assertThat(responseStatus.getBody().message()).isEqualTo("Pago no encontrado");
        assertThat(generic.getStatusCode().value()).isEqualTo(500);
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }
}
