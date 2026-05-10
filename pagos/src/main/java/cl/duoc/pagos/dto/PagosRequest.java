package cl.duoc.pagos.dto;

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
    @NotBlank(message= "El metodo de pago es obligatorio")
    private String metodoPago;

    @NotNull(message= "El monto es obligatorio")
    @Column(nullable= false)
    private double montoAPagar;
}
