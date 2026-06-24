package cl.duoc.ordenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description= "Id al que se asociara la orden", example= "5")
    @NotNull(message= "Tiene que haber un id de usuario vinculado a un carrito")
    private Long usuarioId;
}