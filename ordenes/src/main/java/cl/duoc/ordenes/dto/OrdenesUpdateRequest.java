package cl.duoc.ordenes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenesUpdateRequest {
    @NotBlank(message = "La dirección de destino no puede estar vacía")
    private String direccionEnvio;
    
    @NotBlank(message= "Tiene que elegir una empresa para su envio")
    private String empresaTransporte;
}
