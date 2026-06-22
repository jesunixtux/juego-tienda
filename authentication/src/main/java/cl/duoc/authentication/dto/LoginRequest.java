package cl.duoc.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales para iniciar sesion y obtener un JWT.")
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato valido")
        @Schema(description = "Correo registrado.", example = "jesus@duoc.cl")
        String correo,

        @NotBlank(message = "La password es obligatoria")
        @Schema(description = "Password en texto plano enviada solo para login.", example = "secreto123")
        String password
) {
}
