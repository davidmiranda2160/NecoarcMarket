package cl.duoc.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDetalleDTO {
    private Long productoId;
    private String nombre; 
    private Integer cantidad; 

}
    


