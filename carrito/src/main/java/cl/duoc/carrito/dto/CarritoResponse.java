package cl.duoc.carrito.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarritoResponse {
    private Long id;
    private int cantidad;
    private BigDecimal montoTotal;
    private UsuarioResponse usuario; 
    private ProductoResponse producto;
}