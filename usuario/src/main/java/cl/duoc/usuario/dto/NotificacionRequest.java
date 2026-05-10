package cl.duoc.usuario.dto;

import lombok.Data;

@Data
public class NotificacionRequest {
    private Long usuarioId;
    private String mensaje;
    private String tipo;
}
