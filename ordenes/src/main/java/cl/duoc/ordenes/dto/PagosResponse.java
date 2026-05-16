package cl.duoc.ordenes.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagosResponse {
    private Long id;
    private Long idOrden; 
    private String metodoPago;
    private BigDecimal montoAPagar;
    private double montoPagado;
    private Date fechaTransaccion;
    private String estadoPago;
}
