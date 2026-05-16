package cl.duoc.ordenes.dto;

import java.math.BigDecimal;
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
    private BigDecimal montoTotal;
    private String direccionEnvio;
    private UsuarioResponse usuario;
    private EnvioResponse envio;
    private PagosResponse pagos;
}
