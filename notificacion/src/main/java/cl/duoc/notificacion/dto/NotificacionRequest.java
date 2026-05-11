package cl.duoc.notificacion.dto;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class NotificacionRequest {
    private Long usuarioId;
    private String mensaje;
    private String tipo;
}
