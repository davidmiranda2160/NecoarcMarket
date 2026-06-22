package cl.duoc.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDetalleDTO {

    @Schema(description = "Id del producto que queremos saber su nombre en inventario", example = "1")
    private Long productoId;

    @Schema(description = "nombre del producto que se encuentra en inventario", example = "necoarc")
    private String nombre; 

    @Schema(description = "cantidad del producto del inventario", example = "100")
    private Integer cantidad; 

}
    


