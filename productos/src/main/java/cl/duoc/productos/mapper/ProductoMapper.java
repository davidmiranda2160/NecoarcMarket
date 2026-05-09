package cl.duoc.productos.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.productos.dto.ProductoRequest;
import cl.duoc.productos.dto.ProductoResponse;
import cl.duoc.productos.model.Producto;

@Component
public class ProductoMapper {
    public Producto fromRequest(ProductoRequest request){
        return Producto.builder()
                .nombrep(request.getNombrep())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .categoria(request.getCategoria())
                .activo(request.getActivo())
                .vendedorId(request.getVendedorId())
                .build();
    }
public ProductoResponse toResponse(Producto producto){
    .id(producto.getId())
    .nombrep(producto.getNombrep())
    
}




}
