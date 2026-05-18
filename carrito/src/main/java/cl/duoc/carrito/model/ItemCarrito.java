package cl.duoc.carrito.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_carrito")
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El videojuego es obligatorio")
    private Long videojuegoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 1, message = "El precio unitario debe ser mayor a 0")
    private Integer precioUnitario;

    private Integer subtotal;
    private LocalDateTime fechaAgregado;

    @PrePersist
    public void antesDeGuardar() {
        fechaAgregado = LocalDateTime.now();
        calcularSubtotal();
    }

    @PreUpdate
    public void antesDeActualizar() {
        calcularSubtotal();
    }

    private void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            subtotal = cantidad * precioUnitario;
        }
    }
}
