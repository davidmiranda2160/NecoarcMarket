package cl.duoc.ordenes.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponse {
    private Long id;
    private String nombrep;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String categoria;
    private Boolean activo = true;
    private Long vendedorId;

}
