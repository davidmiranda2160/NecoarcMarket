package cl.duoc.ordenes.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarritoResponse {
    private Long id;
    private Integer cantidad;
    private BigDecimal montoTotal;
}
