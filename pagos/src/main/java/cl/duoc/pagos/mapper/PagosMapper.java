package cl.duoc.pagos.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import cl.duoc.pagos.dto.PagosRequest;
import cl.duoc.pagos.dto.PagosResponse;
import cl.duoc.pagos.model.Pagos;

@Component
public class PagosMapper {

    public Pagos fromRequest(PagosRequest request) {
        return Pagos.builder()
                .idOrden(request.getIdOrden()) 
                .metodoPago(request.getMetodoPago())
                .montoAPagar(request.getMontoAPagar())
                .montoPagado(request.getMontoAPagar())
                .fechaTransaccion(LocalDateTime.now())
                .estadoPago("Realizado")
                .build();
    }

    public PagosResponse toResponse(Pagos pagos) {
        return PagosResponse.builder()
                .id(pagos.getId())
                .idOrden(pagos.getIdOrden())
                .metodoPago(pagos.getMetodoPago())
                .montoAPagar(pagos.getMontoAPagar())
                .montoPagado(pagos.getMontoPagado())
                .fechaTransaccion(pagos.getFechaTransaccion())
                .estadoPago(pagos.getEstadoPago())
                .build();
    }
}
