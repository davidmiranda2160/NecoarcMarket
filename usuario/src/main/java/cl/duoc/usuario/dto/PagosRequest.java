package cl.duoc.usuario.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagosRequest {
    @NotNull(message = "El ID de la orden es obligatorio")
    private Long idOrden; // Añadido para que la DB no falle

    @NotBlank(message= "El metodo de pago es obligatorio")
    private String metodoPago;

    @NotNull(message= "El monto es obligatorio")
    @Column(nullable= false)
    private double montoAPagar;

}
