package cl.duoc.ordenes.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoRequest {
    @Min(value = 1, message = "La cantidad mínima es 1")
    private int cantidad;

    @Min(value = 0, message = "El monto total no puede ser negativo")
    private int montoTotal;
}
