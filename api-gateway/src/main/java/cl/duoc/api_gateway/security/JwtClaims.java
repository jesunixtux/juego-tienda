package cl.duoc.api_gateway.security;

public record JwtClaims(
        Long usuarioId,
        String correo,
        String nombreUsuario,
        String rol,
        long expiraEn
) {
}
