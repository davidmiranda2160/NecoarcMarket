package cl.duoc.busqueda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaResponse {
private String codigoSeguimiento;
    private String estado;       
    private String nombreProducto; 
    private String detalle;
}
