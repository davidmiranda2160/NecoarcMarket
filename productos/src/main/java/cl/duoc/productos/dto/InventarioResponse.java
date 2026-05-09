package cl.duoc.productos.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioResponse {

    private Long id;
    private Long productoId;
    private Integer cantidad;
    private LocalDateTime ultimaActualizacion;

}