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
    @NotNull(message= "Tiene que haber un id de usuario vinculado a un carrito")
    private Long usuarioId;

    @NotBlank(message= "Tiene que que haber un metodo de pago")
    private String metodoPago;
}