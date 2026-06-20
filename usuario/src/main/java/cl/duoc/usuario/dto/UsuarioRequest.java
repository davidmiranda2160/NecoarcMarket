package cl.duoc.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequest {
    
    @Schema(description= "Nombre del usuario", example= "Estaban")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Schema(description= "Apellido paterno del usuario", example= "Quinto")
    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido paterno deben tener entre 2 y 100 caracteres")
    private String appaterno;

    @Schema(description= "Apellido materno del usuario", example= "Gonzales")
    @Size(min = 2, max = 100, message = "Los apellido materno deben tener entre 2 y 100 caracteres")
    private String apmaterno;

    @Schema(description= "Correo del usuario", example= "Esteban.quito@test.cl")
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")    
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String correo;

    @Schema(description= "Direccion del usuario", example= "Calle falsa 123")
    @NotBlank(message= "La direccion no puede estar vacia")
    @Size(min = 10, max = 200)
    private String direccion;

    @Schema(description="Telefono del usuario", example= "+569778996777")
    @NotBlank(message= "El numero de telefono es obligatorio")
    private String telefono;

    @Schema(description= "El tipo de usuario", example= "Cliente")
    @NotBlank(message= "El tipo de usuario es obligatorio")
    private String tipoUsuario;
}
