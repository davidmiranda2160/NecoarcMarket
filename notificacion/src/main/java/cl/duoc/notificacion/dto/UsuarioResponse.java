package cl.duoc.notificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String appaterno;
    private String apmaperno;
    private String correo;
    private String direccion;
    private String telefono;
    private String tipoUsuario;
}
