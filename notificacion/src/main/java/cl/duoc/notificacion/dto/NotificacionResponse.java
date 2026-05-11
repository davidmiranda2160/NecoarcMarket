package cl.duoc.notificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponse {
    private Long id;
    private Long usuarioId;
    private String nombreUsuario;
    private String mensaje;
    private String tipo;
    private String fechaEnvio;
}
