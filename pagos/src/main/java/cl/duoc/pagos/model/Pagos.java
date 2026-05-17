package cl.duoc.pagos.model;

import java.math.BigDecimal;
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
@Data
@Table(name= "pagos")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pagos {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    
    @Column(name= "id_orden", nullable= false)
    private Long idOrden; 

    @Column(name= "metodo_pago", nullable= false)
    private String metodoPago;

    @Column(name = "monto_a_pagar", nullable = false)
    private BigDecimal montoAPagar;

    @Column(name = "monto_pagado",nullable= false)
    private BigDecimal montoPagado;

    @Column(name= "fecha_transaccion", nullable= false)
    private LocalDateTime fechaTransaccion;

    @Column(name= "estado_pago", nullable= false)
    private String estadoPago;
}
