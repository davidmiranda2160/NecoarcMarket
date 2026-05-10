package cl.duoc.notificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponse {
    private Long id;
    private String mensaje;
    private String tipo;
    private String fechaFormateada;
}
