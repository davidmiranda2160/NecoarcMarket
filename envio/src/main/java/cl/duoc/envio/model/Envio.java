package cl.duoc.envio.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "Envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ordenId;

    @Column(nullable= false)
    private String direccionDestino;

    @Column(nullable= false)
    private String numeroSeguimiento;

    @Column(nullable= false)
    private String estadoEnvio;

    @Column(nullable= false)
    private String empresaTransporte;

    @Column(nullable= false)
    private LocalDate fechaEstimadaEntrega;

}
