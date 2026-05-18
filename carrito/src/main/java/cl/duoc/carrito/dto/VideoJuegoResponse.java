package cl.duoc.carrito.dto;

import java.time.LocalDate;

public record VideoJuegoResponse(
        Long id,
        String nombre,
        String categoria,
        Integer precio,
        String plataforma,
        String descripcion,
        String desarrollador,
        LocalDate fechaLanzamiento,
        String imagenUrl,
        Boolean activo
) {
}
