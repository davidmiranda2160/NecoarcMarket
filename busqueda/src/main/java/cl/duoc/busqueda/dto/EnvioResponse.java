package cl.duoc.busqueda.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; //import del json ignore para el ordenes, amenos que lo agregue en response

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) //quitarlo cuando agregue el ordenes response
public class EnvioResponse {
    private Long id;
    private String numeroSeguimiento;
    private String estadoEnvio;
    private String empresaTransporte;
    private LocalDate fechaEstimadaEntrega;
    private String direccionDestino;

    private OrdenesResponse orden; //Esto hasta el momento no lo necesito, por tanto usaré un jsonIgnore a ver si deja de marcar error 500
}