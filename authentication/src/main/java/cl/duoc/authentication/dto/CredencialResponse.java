package cl.duoc.authentication.dto;

import cl.duoc.authentication.model.Credencial;

import java.time.LocalDateTime;

public record CredencialResponse(
        Long id,
        Long usuarioId,
        String correo,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
    public static CredencialResponse from(Credencial credencial) {
        return new CredencialResponse(
                credencial.getId(),
                credencial.getUsuarioId(),
                credencial.getCorreo(),
                credencial.getActivo(),
                credencial.getFechaCreacion(),
                credencial.getFechaActualizacion()
        );
    }
}
