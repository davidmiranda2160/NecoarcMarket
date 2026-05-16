package cl.duoc.ordenes.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoResponse {
    private Long id;
    private int cantidad;
    private BigDecimal montoTotal;
    private UsuarioResponse usuario; 
    private ProductoResponse producto;
}
