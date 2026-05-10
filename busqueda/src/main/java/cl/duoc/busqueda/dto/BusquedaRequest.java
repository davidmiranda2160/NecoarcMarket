package cl.duoc.busqueda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaRequest {

    @NotBlank(message = "El código de seguimiento no puede estar vacío")
    @Size(min = 5, max = 50, message = "El código debe tener entre 5 y 50 caracteres")
    private String codigoSeguimiento;
}
