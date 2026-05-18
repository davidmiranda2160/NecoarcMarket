package cl.duoc.productos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.productos.client.InventarioClient;
import cl.duoc.productos.dto.InventarioRequest;
import cl.duoc.productos.dto.InventarioResponse;
import cl.duoc.productos.dto.ProductoRequest;
import cl.duoc.productos.dto.ProductoResponse;
import cl.duoc.productos.dto.ProductoUpdateRequest;
import cl.duoc.productos.exception.ConflictException;
import cl.duoc.productos.mapper.ProductoMapper;
import cl.duoc.productos.model.Producto;
import cl.duoc.productos.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@Slf4j
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoMapper productoMapper;

    @Autowired
    private InventarioClient inventarioClient;

    
    /*public List<Producto> buscarProductos(){
        log.info("Buscando todos los productos");
        return productoRepository.findAll();
    }
    */
    /*Este es un listar productos que vincula con inventario, consulta por los datos estáticos de productos en la base de datos y
    los trae, pero no contiene el STOCK de producto, ya que se separó el stock en inventario para trabajarse directamente  con la base de datos inventario
    */
    public List<ProductoResponse> buscarProductos(){
        log.info("Buscando todos los productos con sus respectivos stocks");
        List<Producto> productos = productoRepository.findAll();
        List<ProductoResponse> listaConStock = new ArrayList<>();
        for (Producto prod : productos) {
            Integer stockActual = 0;
            try {
                InventarioResponse inventario = inventarioClient.obtenerStockPorProducto(prod.getId());
                if (inventario != null) {
                    stockActual = inventario.getCantidad();
                }
            } catch (Exception e) {
                log.error("No se pudo obtener stock para el producto ID: {} en el listado masivo", prod.getId());
                stockActual = 0;
            }
            ProductoResponse response = productoMapper.toResponse(prod, stockActual);
                        listaConStock.add(response);
        }
        return listaConStock;
    }


    public ProductoResponse buscarProductoPorId(Long id){
        log.info("Buscando producto por ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado"));     
        Integer stockActual = 0;
        try {
            InventarioResponse inventario = inventarioClient.obtenerStockPorProducto(id);
            if (inventario != null) {
                stockActual = inventario.getCantidad();
            }
        } catch (Exception e) {
            log.error("Micro de inventario caído para ID {}, se asigna stock 0", id);
            stockActual = 0;
        }
        return productoMapper.toResponse(producto, stockActual);
    }


    public ProductoResponse buscarProductoPorNombre(String nombrep) {
        log.info("Buscando producto por nombre: {}", nombrep);
        Producto producto = productoRepository.findByNombrep(nombrep)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado"));
        
        Integer stockActual = 0;
        try {
            InventarioResponse inventario = inventarioClient.obtenerStockPorProducto(producto.getId());
            if (inventario != null) {
                stockActual = inventario.getCantidad();
            }
        } catch (Exception e) {
            log.error("Micro de inventario caído para nombre {}, se asigna stock 0", nombrep);
            stockActual = 0;
        }

        return productoMapper.toResponse(producto, stockActual);
    }

    public ProductoResponse crearProducto(ProductoRequest request){
        log.info("Creando producto con nombre: {}", request.getNombrep());
        if(productoRepository.existsByNombrep(request.getNombrep())){
            log.warn("El nombre del producto '{}' ya existe", request.getNombrep());
            throw new ConflictException("El nombre del producto ya existe");
        }        
        
        Producto producto = productoMapper.fromRequest(request);
        producto.setActivo(true);
        producto = productoRepository.save(producto);
        
        try { //Luego moveré esto al mapper
            InventarioRequest inventarioReq = InventarioRequest.builder()
                    .productoId(producto.getId())
                    .cantidad(0) 
                    .build();
          
            inventarioClient.crearRegistroInventario(inventarioReq);
            log.info("Registro de inventario inicializado con éxito para el producto ID: {}", producto.getId());
        } catch (Exception e) {
            log.error("No se pudo inicializar el inventario físico: {}. Se deberá abastecer manualmente.", e.getMessage());
        }        
        return productoMapper.toResponse(producto, 0);
    }

    // Actualiza datos de un producto
    public ProductoResponse actualizarProducto(Long id, ProductoUpdateRequest request) {
        log.info("Actualizando producto con ID: {}", id);      
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado"));

        if (request.getNombrep() != null && productoRepository.existsByNombrepAndIdNot(request.getNombrep(), id)) {
            log.warn("Fallo en el nombre del producto '{}' ya existe", request.getNombrep());
            throw new ConflictException("Ya existe otro producto con ese nombre");
        }        //Quité el stock porque forma parte de inventario
        if (request.getNombrep() != null) producto.setNombrep(request.getNombrep());
        if (request.getDescripcion() != null) producto.setDescripcion(request.getDescripcion());
        if (request.getPrecio() != null) producto.setPrecio(request.getPrecio());
        if (request.getCategoria() != null) producto.setCategoria(request.getCategoria());
        if (request.getActivo() != null) producto.setActivo(request.getActivo());
        if (request.getVendedorId() != null) producto.setVendedorId(request.getVendedorId());

        producto = productoRepository.save(producto);
        Integer stockActual = 0;
        try {
            InventarioResponse inventario = inventarioClient.obtenerStockPorProducto(id);
            if (inventario != null) {
                stockActual = inventario.getCantidad();
            }
        } catch (Exception e) {
            log.error("Inventario caído al actualizar ID {}, se asigna stock 0", id);
            stockActual = 0;
        }
        return productoMapper.toResponse(producto, stockActual);
    }

    // Eliminacion de un producto por id
    public void eliminarProducto(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        if (!productoRepository.existsById(id)) {
            log.warn("Falló la eliminación, producto con ID {} no encontrado", id);
            throw new NoSuchElementException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }   
}
