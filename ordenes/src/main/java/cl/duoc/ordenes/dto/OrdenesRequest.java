package cl.duoc.ordenes.dto;

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

    @NotBlank(message= "El metodo de pago es obligatorio")
    private String metodoPago;

    @NotBlank(message= "Tiene que elegir una empresa de transporte para su envio")
    private String empresaTransporte;
}