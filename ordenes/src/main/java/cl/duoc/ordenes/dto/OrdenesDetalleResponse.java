package cl.duoc.ordenes.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenesDetalleResponse {
    private Long idProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}