package cl.duoc.ordenes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvioRequest {
    @NotBlank(message= "La empresa de transporte es obligatoria")
    private String empresaTransporte;

    @NotBlank(message= "Tiene que haber una direccion para entregar el envio")
    private String direccionDestino;

}
