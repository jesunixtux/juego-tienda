package cl.duoc.authentication.exception;

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
    private final HttpServletRequest request = request("/auth/login");

    @Test
    void handleApiCubreUnauthorizedForbiddenConflictNotFoundYBadGateway() {
        assertThat(handler.handleApi(new UnauthorizedException("Credenciales invalidas"), request).getStatusCode().value()).isEqualTo(401);
        assertThat(handler.handleApi(new ForbiddenException("Credencial desactivada"), request).getStatusCode().value()).isEqualTo(403);
        assertThat(handler.handleApi(new ConflictException("Duplicado"), request).getStatusCode().value()).isEqualTo(409);
        assertThat(handler.handleApi(new ResourceNotFoundException("Usuario no encontrado"), request).getStatusCode().value()).isEqualTo(404);
        assertThat(handler.handleApi(new ExternalServiceException("Servicio externo"), request).getStatusCode().value()).isEqualTo(502);
    }

    @Test
    void handleResponseStatusYGenericDevuelvenErrorControlado() {
        ResponseEntity<ErrorResponse> notFound = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontrado"),
                request);
        ResponseEntity<ErrorResponse> generic = handler.handleGeneric(new RuntimeException("boom"), request);

        assertThat(notFound.getBody().message()).isEqualTo("No encontrado");
        assertThat(generic.getStatusCode().value()).isEqualTo(500);
        assertThat(generic.getBody().message()).isEqualTo("Error interno del servidor");
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }
}
