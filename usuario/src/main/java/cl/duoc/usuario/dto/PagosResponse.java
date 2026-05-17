package cl.duoc.usuario.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagosResponse {
    private Long id;
    private Long idOrden; 
    private String metodoPago;
    private double montoAPagar;
    private double montoPagado;
    private Date fechaTransaccion;
    private String estadoPago;
}
