package cl.duoc.authentication.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticacion funcional con datos del usuario.")
@JsonPropertyOrder({"nombreUsuario", "correo", "rol", "mensaje", "autenticado", "usuarioId"})
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
        Long usuarioId
) {
}
