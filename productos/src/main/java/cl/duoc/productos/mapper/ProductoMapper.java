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
                .categoria(request.getCategoria())
                .vendedorId(request.getVendedorId())
                .activo(true)
                .build();
    }

    //En caso de que ya no haya stock
    public ProductoResponse toResponse(Producto producto){
        return toResponse(producto, 0); 
    }


    public ProductoResponse toResponse(Producto producto, Integer stockReal){
        return ProductoResponse.builder()
                .id(producto.getId())
                .nombrep(producto.getNombrep())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(stockReal)//Vinculo a inventario
                .categoria(producto.getCategoria())
                .activo(producto.getActivo())
                .vendedorId(producto.getVendedorId())
                .build();
    }
}
