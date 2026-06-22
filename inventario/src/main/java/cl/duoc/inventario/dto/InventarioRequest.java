package cl.duoc.inventario.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequest {

    @Schema(description = "Id del producto que se quiera abastecer", example = "1")
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @Schema(description = "cantidad del producto que se quiera agregar", example = "100")
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;

}
