package cl.duoc.authentication.dto;

public record UsuarioRequest(
        String nombre,
        String apellido,
        String correo,
        String telefono,
        String direccion,
        String rol,
        Boolean activo
) {
}
