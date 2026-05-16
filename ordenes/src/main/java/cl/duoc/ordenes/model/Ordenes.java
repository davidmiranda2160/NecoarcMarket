package cl.duoc.ordenes.model;

import java.math.BigDecimal;
import java.util.Date;

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
@Data
@Table(name= "ordenes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ordenes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false)
    private Long idUsuario;

    @Column(nullable= false)
    private Long idEnvio;

    @Column(nullable= false)
    private Date fechaCreacion;

    @Column(nullable= false)
    private String estadoOrden;

    @Column(nullable= false)
    private BigDecimal montoTotal;

    @Column(nullable= false)
    private String direccionEnvio;

    @Column(nullable= false)
    private Long idPago;
}