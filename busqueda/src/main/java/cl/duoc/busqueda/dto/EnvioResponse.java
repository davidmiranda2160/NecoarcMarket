package cl.duoc.busqueda.dto;

import java.time.LocalDate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnvioResponse {
    private Long id;
    private String numeroSeguimiento;
    private String estadoEnvio;
    private String empresaTransporte;
    private LocalDate fechaEstimadaEntrega;
    private String direccionDestino;

}