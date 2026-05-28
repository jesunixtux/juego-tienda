package cl.duoc.resenas.exception;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends ApiException {
    public ExternalServiceException(String message) {
        super(HttpStatus.BAD_GATEWAY, message);
    }
}
