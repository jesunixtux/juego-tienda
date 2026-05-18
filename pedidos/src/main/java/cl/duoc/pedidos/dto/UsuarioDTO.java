package cl.duoc.pedidos.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String direccion;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}
