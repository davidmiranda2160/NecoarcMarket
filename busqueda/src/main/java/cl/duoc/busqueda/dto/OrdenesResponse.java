package cl.duoc.busqueda.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdenesResponse {
    private Long id;
    private Long idUsuario;
    private Date fechaCreacion;
    private String estadoOrden;
    private double montoTotal;
    private String direccionEnvio;
    private Long idPago;
}
