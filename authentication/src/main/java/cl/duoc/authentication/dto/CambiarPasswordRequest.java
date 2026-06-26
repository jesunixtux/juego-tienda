package cl.duoc.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para cambiar la password de una credencial.")
public record CambiarPasswordRequest(
        @NotBlank(message = "La contrasena invalida: la password actual es obligatoria")
        @Size(min = 5, message = "La contrasena invalida: la password actual debe tener minimo 5 caracteres")
        @Schema(
                description = "Password actual para validar identidad. Minimo 5 caracteres.",
                example = "clave123",
                minLength = 5,
                requiredMode = Schema.RequiredMode.REQUIRED)
        String passwordActual,

        @NotBlank(message = "La contrasena invalida: la nueva password es obligatoria")
        @Size(min = 5, message = "La contrasena invalida: la nueva password debe tener minimo 5 caracteres")
        @Schema(
                description = "Nueva password. Debe tener minimo 5 caracteres.",
                example = "nuevaClave123",
                minLength = 5,
                requiredMode = Schema.RequiredMode.REQUIRED)
        String nuevaPassword
) {
}
