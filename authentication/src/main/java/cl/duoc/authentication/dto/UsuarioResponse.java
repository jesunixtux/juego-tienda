package cl.duoc.authentication.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String correo,
        String telefono,
        String direccion,
        String rol,
        Boolean activo,
        LocalDateTime fechaRegistro
) {
}
