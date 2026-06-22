package cl.duoc.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para cambiar la password de una credencial.")
public record CambiarPasswordRequest(
        @NotBlank(message = "La password actual es obligatoria")
        @Schema(description = "Password actual para validar identidad.", example = "secreto123")
        String passwordActual,

        @NotBlank(message = "La nueva password es obligatoria")
        @Size(min = 6, message = "La nueva password debe tener al menos 6 caracteres")
        @Schema(description = "Nueva password. Debe tener minimo 6 caracteres.", example = "nuevoSecreto123")
        String nuevaPassword
) {
}
