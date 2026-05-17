package cl.duoc.ordenes.model;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ordenes")
@Builder
public class Ordenes {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name= "usuario_id", nullable= false)
    private Long usuarioId;

    @Column(name="estado_orden", nullable= false)
    private String estadoOrden;

    @Column(name= "total", nullable=false)
    private BigDecimal total;

    @Column(name= "fecha_orden", nullable= false)
    private LocalDateTime fechaOrden;
}