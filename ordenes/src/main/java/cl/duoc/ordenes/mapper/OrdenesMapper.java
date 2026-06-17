package cl.duoc.ordenes.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cl.duoc.ordenes.dto.CarritoResponse;
import cl.duoc.ordenes.dto.OrdenesDetalleResponse;
import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.model.Ordenes;
import cl.duoc.ordenes.model.OrdenesDetalle;

@Component
public class OrdenesMapper {

    public Ordenes fromRequest(OrdenesRequest request, BigDecimal totalCalculado) {
        return Ordenes.builder()
                .usuarioId(request.getUsuarioId())
                .estadoOrden("Pendiente")
                .total(totalCalculado)
                .fechaOrden(LocalDateTime.now())
                .build();
    }

    public OrdenesDetalle toDetalleEntity(CarritoResponse carritoItem, Long ordenId) {
        return OrdenesDetalle.builder()
                .ordenId(ordenId)
                .idProducto(carritoItem.getProducto().getId())
                .cantidad(carritoItem.getCantidad())
                .precioUnitario(carritoItem.getProducto().getPrecio())
                .build();
    }

    public OrdenesDetalleResponse toDetalleResponse(OrdenesDetalle detalle) {
        return OrdenesDetalleResponse.builder()
                .idProducto(detalle.getIdProducto())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .build();
    }

    public OrdenesResponse toResponse(Ordenes orden, List<OrdenesDetalle> detalles) {
        List<OrdenesDetalleResponse> detallesDto = null;
        if (detalles != null) {
            detallesDto = detalles.stream()
                    .map(this::toDetalleResponse)
                    .collect(Collectors.toList());
        }

        return OrdenesResponse.builder()
                .id(orden.getId())
                .usuarioId(orden.getUsuarioId())
                .total(orden.getTotal())
                .estadoOrden(orden.getEstadoOrden())
                .fechaOrden(orden.getFechaOrden())
                .detalles(detallesDto)
                .build();
    }
}