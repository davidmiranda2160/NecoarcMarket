package cl.duoc.pagos.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.pagos.dto.PagosRequest;
import cl.duoc.pagos.dto.PagosResponse;
import cl.duoc.pagos.model.Pagos;

@Component
public class PagosMapper {

    public Pagos fromRequest(PagosRequest request) {
        return Pagos.builder()
                .metodoPago(request.getMetodoPago())
                .montoAPagar(request.getMontoAPagar())
                .build();
    }

    public PagosResponse toResponse(Pagos pagos) {
        return PagosResponse.builder()
                .idPedido(pagos.getIdPedido())
                .metodoPago(pagos.getMetodoPago())
                .montoAPagar(pagos.getMontoAPagar())
                .montoPagado(pagos.getMontoPagado())
                .fechaTransaccion(pagos.getFechaTransaccion())
                .estadoPago(pagos.getEstadoPago())
                .build();
    }
}
