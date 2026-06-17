package cl.duoc.carrito.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.carrito.dto.CarritoRequest;
import cl.duoc.carrito.dto.CarritoResponse;
import cl.duoc.carrito.dto.ProductoResponse;
import cl.duoc.carrito.dto.UsuarioResponse;
import cl.duoc.carrito.model.Carrito;

@Component
public class CarritoMapper {
       public Carrito fromRequest(CarritoRequest request, Long idUsuario,Long idProducto) {
                return Carrito.builder()
                .idUsuario(idUsuario)  
                .idProducto(idProducto) 
                .cantidad(request.getCantidad())
                .build();
       }

    public CarritoResponse toResponse(Carrito  carrito, UsuarioResponse usuario, ProductoResponse producto) {
                return CarritoResponse.builder()
                .id(carrito.getId())
                .cantidad(carrito.getCantidad())
                .montoTotal(carrito.getMontoTotal())
                .usuario(usuario)   
                .producto(producto) 
                .build();
    }
}
