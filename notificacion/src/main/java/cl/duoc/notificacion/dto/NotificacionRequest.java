package cl.duoc.notificacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequest {

    @Schema(description = "id de usuario automatico")
    private Long usuarioId;

    @Schema(description = "notificacion a añadir", example = "tiene una oferta para el peluche de necoarc con un 40% de descuento en pago con tarjeta")
    private String mensaje;

    @Schema(description = "tipo de notificacion a agregar", example = "Nuevo producto")
    private String tipo;


}
