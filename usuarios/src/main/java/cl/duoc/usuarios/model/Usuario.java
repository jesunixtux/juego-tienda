package cl.duoc.usuarios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
@Schema(description = "Usuario registrado en la tienda de videojuegos.")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador autogenerado del usuario.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del usuario.", example = "Jesus")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Schema(description = "Apellido del usuario.", example = "Emilio")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato valido")
    @Column(nullable = false, unique = true)
    @Schema(description = "Correo unico usado por el usuario.", example = "jesus@duoc.cl")
    private String correo;

    @Schema(description = "Telefono de contacto.", example = "+56912345678")
    private String telefono;
    @Schema(description = "Direccion de despacho o contacto.", example = "Av. Siempre Viva 742")
    private String direccion;

    @NotBlank(message = "El rol es obligatorio")
    @Schema(description = "Rol funcional del usuario.", example = "CLIENTE", allowableValues = {"CLIENTE", "ADMIN"})
    private String rol;

    @Schema(description = "Indica si el usuario puede operar en el sistema.", example = "true")
    private Boolean activo = true;
    @Schema(description = "Fecha de registro generada automaticamente.", example = "2026-06-22T16:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void antesDeGuardar() {
        if (activo == null) {
            activo = true;
        }

        fechaRegistro = LocalDateTime.now();
    }
}
