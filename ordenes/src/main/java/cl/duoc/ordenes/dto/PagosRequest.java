package cl.duoc.ordenes.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagosRequest {
    @NotBlank(message= "El metodo de pago es obligatorio")
    private String metodoPago;

    @NotNull(message= "El monto es obligatorio")
    @Column(nullable= false)
    private double montoAPagar;

}
