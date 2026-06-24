package cl.duoc.envio.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name= "envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(name = "direccion_destino", nullable= false)
    private String direccionDestino;

    @Column(name = "numero_seguimiento", nullable= false)
    private String codigoSeguimiento;

    @Column(name= "estado_envio", nullable= false)
    private String estadoEnvio;

    @Column(name = "empresa_transporte",nullable= false)
    private String empresaTransporte;

    @Column(name = "fecha_estimada_entrega", nullable= false)
    private LocalDate fechaEstimadaEntrega;

    @Column(name = "fecha_creacion", nullable= false)
    private LocalDateTime fechaCreacion;

}
