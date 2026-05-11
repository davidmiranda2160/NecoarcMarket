package cl.duoc.busqueda.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "El código de seguimiento no puede estar vacío")
    @Size(min = 5, max = 50, message = "El código debe tener entre 5 y 50 caracteres")
    private String codigoSeguimiento;

    @NotBlank(message = "El estado de envío no puede estar vacío")
    private String estadoEnvio;    
    
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombreProducto; 

    @NotBlank(message = "Siempre debe haber detalle del producto")
    private String detalle;

    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDateTime fechaActualizacionProducto;
}
