package cl.duoc.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar un usuario y crear su credencial.")
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Schema(description = "Nombre del nuevo usuario.", example = "Jesus")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Schema(description = "Apellido del nuevo usuario.", example = "Emilio")
        String apellido,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato valido")
        @Schema(description = "Correo unico para login.", example = "jesus@duoc.cl")
        String correo,

        @Schema(description = "Telefono de contacto.", example = "+56912345678")
        String telefono,

        @Schema(description = "Direccion del usuario.", example = "Av. Siempre Viva 742")
        String direccion,

        @NotBlank(message = "El rol es obligatorio")
        @Schema(description = "Rol inicial del usuario.", example = "CLIENTE", allowableValues = {"CLIENTE", "ADMIN"})
        String rol,

        @NotBlank(message = "La password es obligatoria")
        @Size(min = 6, message = "La password debe tener al menos 6 caracteres")
        @Schema(description = "Password inicial. Se guarda hasheada con SHA-256 y sal.", example = "secreto123")
        String password
) {
}
