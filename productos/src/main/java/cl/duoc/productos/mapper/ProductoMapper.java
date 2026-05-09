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
                .vendedorId(request.getVendedorId())
                .activo(true)
                .build();
    }
public ProductoResponse toResponse(Producto producto){
    return ProductoResponse.builder()
    .id(producto.getId())
    .nombrep(producto.getNombrep())
    .descripcion(producto.getDescripcion())
    .precio(producto.getPrecio())
    .stock(producto.getStock())
    .categoria(producto.getCategoria())
    .vendedorId(producto.getVendedorId())
    .build();
    
}




}
