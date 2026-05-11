package cl.duoc.envio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdenesRequest {
    @NotNull(message= "El id de usuario es obligatorio")
    private Long idUsuario;

    @NotBlank(message="La direccion de envio es obligatoria")
    private String direccionEnvio;

}