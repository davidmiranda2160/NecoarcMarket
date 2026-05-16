package cl.duoc.busqueda.model;

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
@Table(name = "busqueda")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Busqueda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_seguimiento", unique = true)
    private String codigoSeguimiento;

    //añado comentario para que no se me olvide que acá usaré esto para conectar con envios de david
    @Column(name = "envio_id", nullable = false)
    private Long envioId;

    @Column(name = "estado_envio")
    private String estadoEnvio;

    private String detalle;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacionProducto;

}
