package cl.duoc.envio.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String codigoSeguimiento;
    private String estadoEnvio;
    private String empresaTransporte;
    private LocalDate fechaEstimadaEntrega;
    private String direccionDestino;
    private LocalDateTime fechaCreacion;

    private OrdenesResponse orden;
}