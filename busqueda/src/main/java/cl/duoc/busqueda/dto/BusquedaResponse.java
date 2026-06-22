package cl.duoc.busqueda.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusquedaResponse {

    @Schema(description = "id autoincremental", example = "1")
    private Long id;

    @Schema(description = "codigo de seguimiento del producto", example = "TRACK-NECO-004")
    private String codigoSeguimiento;

    @Schema(description = "id del envio", example = "1")
    private Long envioId;

    @Schema(description = "estado del envio", example = "a 1 mt de su casa")
    private String estadoEnvio;       

    @Schema(description = "nombre del producto", example = "peluche necoarc")
    private String nombreProducto; 

    @Schema(description = "detalle del producto", example = "peluche 100% algodon")
    private String detalle;

    @Schema(description = "Fecha actual de la consulta")
    private LocalDateTime fechaActualizacionProducto;
}
