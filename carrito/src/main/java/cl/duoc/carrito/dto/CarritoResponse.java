package cl.duoc.carrito.dto;

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
    private Long idUsuario;
    private Long idProducto;
    private int cantidad;
    private int montoTotal;
}
