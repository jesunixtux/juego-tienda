package cl.duoc.authentication.dto;

public record AuthResponse(
        Long usuarioId,
        String correo,
        String rol,
        String mensaje,
        Boolean autenticado
) {
}
