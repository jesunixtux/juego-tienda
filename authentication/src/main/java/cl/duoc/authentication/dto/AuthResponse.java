package cl.duoc.authentication.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"nombreUsuario", "correo", "rol", "mensaje", "autenticado", "usuarioId", "token", "tipoToken", "expiraEnSegundos"})
public record AuthResponse(
        String nombreUsuario,
        String correo,
        String rol,
        String mensaje,
        Boolean autenticado,
        Long usuarioId,
        String token,
        String tipoToken,
        Long expiraEnSegundos
) {
}
