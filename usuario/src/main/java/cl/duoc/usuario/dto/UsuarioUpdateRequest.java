package cl.duoc.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioUpdateRequest {

    @Schema(description = "Nombre opcional a actualizar", example = "Esteban")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Schema(description = "Apellido paterno opcional a actualizar", example = "Quinto")
    @Size(min = 2, max = 100, message = "El apellido paterno debe tener entre 2 y 100 caracteres")
    private String appaterno;

    @Schema(description = "Apellido materno opcional a actualizar", example = "Gonzales")
    @Size(min = 2, max = 100, message = "El apellido materno debe tener entre 2 y 100 caracteres")
    private String apmaterno;

    @Schema(description = "Correo opcional a actualizar", example = "e.quintogonzales@gmail.com")
    @Email(message = "El correo debe ser válido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String correo;

    @Schema(description = "Dirección opcional a actualizar", example = "Avenida Siempre Viva 742")
    @Size(min = 10, max = 200, message = "La dirección debe tener entre 10 y 200 caracteres")
    private String direccion;

    @Schema(description = "Teléfono opcional a actualizar", example = "+569912345678")
    private String telefono;

    @Schema(description = "Tipo de usuario opcional a actualizar", example = "Vendedor")
    private String tipoUsuario;
}
