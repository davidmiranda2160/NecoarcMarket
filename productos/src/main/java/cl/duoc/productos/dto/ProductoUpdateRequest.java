package cl.duoc.productos.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoUpdateRequest {

    @Size(min = 2, max = 150, message = "Los nombres deben tener entre 2 y 100 caracteres")
    private String nombrep;

    @Size(min = 10, max = 100)
    private String descripcion;

    @DecimalMin(value = "0.0",inclusive = false )
    private BigDecimal precio;

    @Min(value = 0)
    private Integer stock;
    private String categoria;
    private Boolean activo = true;
    private Long vendedorId;



}
