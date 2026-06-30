package cl.duoc.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Credenciales para iniciar sesion.")
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato valido")
        @Schema(description = "Correo registrado.", example = "jesus@duoc.cl")
        String correo,

        @NotBlank(message = "La contrasena invalida: es obligatoria")
        @Size(min = 5, message = "La contrasena invalida: debe tener minimo 5 caracteres")
        @Schema(
                description = "Password en texto plano enviada solo para login. Minimo 5 caracteres.",
                example = "clave123",
                minLength = 5,
                requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
