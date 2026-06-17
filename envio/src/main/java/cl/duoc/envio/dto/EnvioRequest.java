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
public class EnvioRequest {

    @NotNull(message= "Tiene que haber una orden a la que asociar el envio")
    private Long ordenId;

    @NotNull(message= "Se tiene que saber a quien va dirigido el envio")
    private Long usuarioId;

    @NotBlank(message= "La empresa de transporte es obligatoria")
    private String empresaTransporte;

    @NotBlank(message= "Tiene que haber una direccion para entregar el envio")
    private String direccionDestino;
}
