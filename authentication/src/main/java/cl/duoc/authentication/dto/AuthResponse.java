package cl.duoc.authentication.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"nombreUsuario", "correo", "rol", "mensaje", "autenticado", "usuarioId"})
public record AuthResponse(
        String nombreUsuario,
        String correo,
        String rol,
        String mensaje,
        Boolean autenticado,
        Long usuarioId
) {
}
