package cl.duoc.productos.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequest {
    @Schema(description = "nombre producto", example = "necoarc")
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombrep;

    @Schema(description = "descripcion del producto", example = "figura de accion articulada")
    @Size(max = 500, message = "La descripcion no puede tener mas de 500 caracteres")
    private String descripcion;

    @Schema(description = "Valor del producto", example = "20.99")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Schema(description = "categoría del producto (puede estar vacía)", example = "figura")
    private String categoria;

    @Schema(description = "id del vendedor", example = "1")
    @NotNull(message = "El ID del vendedor es obligatorio")
    private Long vendedorId;


}
