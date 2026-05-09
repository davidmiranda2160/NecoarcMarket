package cl.duoc.inventario.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.inventario.dto.InventarioRequest;
import cl.duoc.inventario.dto.InventarioResponse;
import cl.duoc.inventario.model.Inventario;

@Component
public class InventarioMapper {

    public Inventario fromRequest(InventarioRequest request){
        return Inventario.builder()
                .productoId(request.getProductoId())
                .cantidad(request.getCantidad())
                .ultimaActualizacion(java.time.LocalDateTime.now())
                .build();
    }

    public InventarioResponse toResponse(Inventario inventario){
        return InventarioResponse.builder()
                .id(inventario.getId())
                .productoId(inventario.getProductoId())
                .cantidad(inventario.getCantidad())
                .ultimaActualizacion(inventario.getUltimaActualizacion())
                .build();

    }
}
