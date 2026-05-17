package cl.duoc.ordenes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagosResponse {
    private Long id;
    private Long idOrden; 
    private String metodoPago;
    private BigDecimal montoAPagar;
    private BigDecimal montoPagado;
    private LocalDateTime fechaTransaccion;
    private String estadoPago;
}