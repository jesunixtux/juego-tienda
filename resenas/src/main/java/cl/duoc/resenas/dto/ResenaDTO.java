package cl.duoc.resenas.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ResenaDTO {
    private Long id;
    private Long usuarioId;
    private String nombreJuego;
    private String comentario;
    private Integer puntuacion;
    private LocalDate fechaResena;
    private String nombreUsuario;
    private String correoUsuario;
}
