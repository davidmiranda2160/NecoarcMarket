package cl.duoc.carrito.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarritoRequest {

/*
La clase CarritoRequest representa los datos que el cliente
Debe entregar al sistema por lo tanto no entregamos el id
ya que es algo que el sistema generara de manera automatica
*/

    @Min(value = 1, message = "La cantidad mínima es 1")
    private int cantidad;

    @Min(value = 0, message = "El monto total no puede ser negativo")
    private BigDecimal montoTotal;
}
