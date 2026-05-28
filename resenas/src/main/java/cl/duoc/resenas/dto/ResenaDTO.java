package cl.duoc.resenas.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonPropertyOrder({
        "nombreUsuario",
        "nombreJuego",
        "comentario",
        "puntuacion",
        "fechaResena",
        "id",
        "usuarioId",
        "correoUsuario"
})
public class ResenaDTO {
    private String nombreUsuario;
    private String nombreJuego;
    private String comentario;
    private Integer puntuacion;
    private LocalDate fechaResena;
    private Long id;
    private Long usuarioId;
    private String correoUsuario;
}
