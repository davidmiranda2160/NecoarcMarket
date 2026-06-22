package cl.duoc.busqueda.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusquedaRequest {

    @Schema(description = "Codigo de seguimiento para añadir", example = "TRACK-NECO-005")
    @NotBlank(message = "El código de seguimiento no puede estar vacío")
    @Size(min = 5, max = 50, message = "El código debe tener entre 5 y 50 caracteres")
    private String codigoSeguimiento;

    @Schema(description = "Estado del envio", example = "en camino")
    @NotBlank(message = "El estado de envío no puede estar vacío")
    private String estadoEnvio;   
    
    @Schema(description = "id del envío", example = "1")
    @NotNull(message = "El ID de envío externo es obligatorio")
    private Long envioId;
    
    @Schema(description = "nombre del producto en envio", example = "Peluche Neco-Arc")
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombreProducto; 

    @Schema(description = "Detalle del producto", example = "peluche 100% algodón")
    @NotBlank(message = "Siempre debe haber detalle del producto")
    private String detalle;

    @Schema(description = "fecha actual de la consulta")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDateTime fechaActualizacionProducto;
}
