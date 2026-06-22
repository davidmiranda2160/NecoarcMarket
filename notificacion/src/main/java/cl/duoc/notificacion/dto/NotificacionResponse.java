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
public class NotificacionResponse {
    @Schema(description = "id automatico incremental de la notificacion", example = "1")
    private Long id;

    @Schema(description = "id automatico del usuario en notificaciones", example ="1" )
    private Long usuarioId;

    @Schema(description = "nombre del usuario al que se le da la notificacion", example = "Juan")
    private String nombreUsuario;

    @Schema(description = "apellido paterno del usuario")
    private String appaternouno;

    @Schema(description = "Mensaje que se va a enviar al usuario", example = "figura de necoarc en descuento")
    private String mensaje;

    @Schema(description = "tipo de mensaje asociado a la notificacion", example = "Oferta")
    private String tipo;

    @Schema(description = "Fecha automatica asignada con NOW")
    private String fechaEnvio;
}
