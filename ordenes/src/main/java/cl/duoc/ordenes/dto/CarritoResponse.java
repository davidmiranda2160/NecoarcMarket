package cl.duoc.ordenes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoResponse {
    private Long id;
    private int cantidad;
    private int montoTotal;
    private UsuarioResponse usuario; 
    private ProductoResponse producto;
}
