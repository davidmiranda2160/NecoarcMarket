package cl.duoc.pagos.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description= "Id al que se asociara el pago", example= "5")
    @NotNull(message = "El ID de la orden es obligatorio")
    private Long idOrden; 

    @Schema(description= "Metodo de pago que usara el usuario", example= "Web-pay")
    @NotBlank(message= "El metodo de pago es obligatorio")
    private String metodoPago;

    @Schema(description= "Monto que pagara el usuario", example= "129.95")
    @NotNull(message= "El monto es obligatorio")
    private BigDecimal montoAPagar;
}
