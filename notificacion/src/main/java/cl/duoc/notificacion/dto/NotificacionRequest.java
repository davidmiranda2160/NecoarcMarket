package cl.duoc.notificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequest {
    private Long usuarioId;
    private String mensaje;
    private String tipo;
}
