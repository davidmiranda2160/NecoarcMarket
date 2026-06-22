package cl.duoc.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String appaterno;
    private String apmaterno;
    private String correo;
    private String direccion;
    private String telefono;
    private String tipoUsuario;

}
