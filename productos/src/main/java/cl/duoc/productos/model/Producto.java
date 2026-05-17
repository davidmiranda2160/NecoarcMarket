package cl.duoc.productos.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombrep;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    //para no comerse decimales
    @Column(nullable = false)
    @DecimalMin(value="0.0", inclusive = false)
    private BigDecimal precio;

    @Column(length = 255)
    private String categoria;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "vendedor_id", nullable = false)
    private Long vendedorId; 

}
