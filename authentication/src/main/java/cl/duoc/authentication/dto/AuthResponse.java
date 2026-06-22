package cl.duoc.authentication.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticacion con datos de usuario y JWT.")
@JsonPropertyOrder({"nombreUsuario", "correo", "rol", "mensaje", "autenticado", "usuarioId", "token", "tipoToken", "expiraEnSegundos"})
public record AuthResponse(
        @Schema(description = "Nombre completo del usuario autenticado.", example = "Jesus Emilio")
        String nombreUsuario,
        @Schema(description = "Correo autenticado.", example = "jesus@duoc.cl")
        String correo,
        @Schema(description = "Rol del usuario.", example = "CLIENTE")
        String rol,
        @Schema(description = "Mensaje funcional de la operacion.", example = "Login exitoso")
        String mensaje,
        @Schema(description = "Indica si la autenticacion fue exitosa.", example = "true")
        Boolean autenticado,
        @Schema(description = "ID del usuario asociado.", example = "1")
        Long usuarioId,
        @Schema(description = "JWT que se debe enviar en Swagger/Postman como Bearer token.", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,
        @Schema(description = "Tipo de token.", example = "Bearer")
        String tipoToken,
        @Schema(description = "Tiempo de expiracion en segundos.", example = "7200")
        Long expiraEnSegundos
) {
}
