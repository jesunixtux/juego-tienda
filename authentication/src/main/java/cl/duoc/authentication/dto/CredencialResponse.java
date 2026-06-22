package cl.duoc.authentication.dto;

import cl.duoc.authentication.model.Credencial;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Credencial sin exponer passwordHash.")
public record CredencialResponse(
        @Schema(description = "ID de la credencial.", example = "1")
        Long id,
        @Schema(description = "ID del usuario asociado.", example = "1")
        Long usuarioId,
        @Schema(description = "Correo usado para login.", example = "jesus@duoc.cl")
        String correo,
        @Schema(description = "Indica si la credencial puede iniciar sesion.", example = "true")
        Boolean activo,
        @Schema(description = "Fecha de creacion.", example = "2026-06-22T16:30:00")
        LocalDateTime fechaCreacion,
        @Schema(description = "Fecha de ultima actualizacion.", example = "2026-06-22T16:45:00")
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
