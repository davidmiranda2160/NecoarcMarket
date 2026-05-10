package cl.duoc.carrito.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.carrito.dto.CarritoRequest;
import cl.duoc.carrito.dto.CarritoResponse;
import cl.duoc.carrito.model.Carrito;

@Component
public class CarritoMapper {
       public Carrito fromRequest(CarritoRequest request) {
                return Carrito.builder()
                .idUsuario(request.getIdUsuario())
                .idProducto(request.getIdProducto())
                .cantidad(request.getCantidad())
                .montoTotal(request.getMontoTotal())
                .build();
       }

    public CarritoResponse toResponse(Carrito  carrito) {
                return CarritoResponse.builder()
                .id(carrito.getId())
                .idUsuario(carrito.getIdUsuario())
                .idProducto(carrito.getIdProducto())
                .cantidad(carrito.getCantidad())
                .montoTotal(carrito.getMontoTotal())
                .build();
    }
}
