package cl.duoc.busqueda.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusquedaResponse {
    private Long id;
    private String codigoSeguimiento;

    private String estadoEnvio;       
    private String nombreProducto; 
    private String detalle;
    private LocalDateTime fechaActualizacionProducto;
}
