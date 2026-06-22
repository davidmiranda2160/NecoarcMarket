package cl.duoc.inventario.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponse {

    @Schema(description = "ID único del producto en la base de datos", example = "1")
    private Long id;

    @Schema(description = "Nombre oficial del producto", example = "Peluche de Neco-Arc")
    private String nombrep;

    @Schema(description = "Detalle o descripción comercial del producto", example = "Peluche oficial de 20cm, edición limitada Burunyuu.")
    private String descripcion;

    @Schema(description = "Precio unitario del producto en la tienda", example = "25.99")
    private BigDecimal precio;

    @Schema(description = "Stock real disponible consultado dinámicamente al microservicio de inventario", example = "45")
    private Integer stock;

    @Schema(description = "Categoría a la que pertenece el producto", example = "Coleccionables")
    private String categoria;

    @Schema(description = "Estado de disponibilidad calculado (true si el stock es mayor a 0)", example = "true")
    private Boolean activo = true;

    @Schema(description = "ID del vendedor dueño del producto", example = "1")
    private Long vendedorId;

}
