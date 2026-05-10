package cl.duoc.pagos.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pagos {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false)
    private Long idPedido; 

    @Column(nullable= false)
    private String metodoPago;

    @Column(nullable= false)
    private double montoAPagar;

    @Column(nullable= false)
    private double montoPagado;

    @Column(nullable= false)
    private Date fechaTransaccion;

    @Column(nullable= false)
    private String estadoPago;
}
