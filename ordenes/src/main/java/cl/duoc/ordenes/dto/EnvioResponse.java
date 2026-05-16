package cl.duoc.ordenes.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvioResponse {
    private Long id;
    private String numeroSeguimiento;
    private String estadoEnvio;
    private String empresaTransporte;
    private LocalDate fechaEstimadaEntrega;
    private String direccionDestino;
    private OrdenesResponse orden;
}
