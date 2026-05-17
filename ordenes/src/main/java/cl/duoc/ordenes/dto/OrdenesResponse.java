package cl.duoc.ordenes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdenesResponse {
    private Long id;
    private Long usuarioId;
    private BigDecimal total;
    private String estadoOrden;
    private LocalDateTime fechaOrden;
    private List<CarritoResponse> items; 
}
