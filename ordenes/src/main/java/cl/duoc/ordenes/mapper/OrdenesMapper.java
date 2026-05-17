package cl.duoc.ordenes.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import cl.duoc.ordenes.dto.CarritoResponse;
import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.model.Ordenes;

@Component
public class OrdenesMapper {

    public Ordenes fromRequest(OrdenesRequest request, BigDecimal totalCalculado){
        if(request == null){
            return null;
        }

        return Ordenes.builder()
                .usuarioId(request.getUsuarioId())
                .total(totalCalculado)
                .estadoOrden("PENDIENTE")
                .fechaOrden(LocalDateTime.now())
                .build();
    }

    public OrdenesResponse toResponse(Ordenes ordenes, List<CarritoResponse> itemsCarrito){
        if(ordenes == null){
            return null;
        }

        return OrdenesResponse.builder()
                .id(ordenes.getId())
                .usuarioId(ordenes.getUsuarioId())
                .total(ordenes.getTotal())
                .estadoOrden(ordenes.getEstadoOrden())
                .fechaOrden(ordenes.getFechaOrden())
                .items(itemsCarrito) //con este mostramos el carrito en la orden
                .build();
    }

}
