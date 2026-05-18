package cl.duoc.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarPasswordRequest(
        @NotBlank(message = "La password actual es obligatoria")
        String passwordActual,

        @NotBlank(message = "La nueva password es obligatoria")
        @Size(min = 6, message = "La nueva password debe tener al menos 6 caracteres")
        String nuevaPassword
) {
}
