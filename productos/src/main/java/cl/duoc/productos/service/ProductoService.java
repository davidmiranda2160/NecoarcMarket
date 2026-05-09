package cl.duoc.productos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.productos.dto.ProductoRequest;
import cl.duoc.productos.dto.ProductoResponse;
import cl.duoc.productos.exception.ConflictException;
import cl.duoc.productos.mapper.ProductoMapper;
import cl.duoc.productos.model.Producto;
import cl.duoc.productos.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

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
//Debo poner el cliente
    //Busca todos los productos disponibles
    public List<Producto> buscarProductos(){
        log.info("Buscando todos los productos");
        return productoRepository.findAll();
    }

    //Buscar producto por ID
    //Revisar producto response (o hacerlo)
    public ProductoResponse buscarProductoPorId(Long id){
        log.info("Buscando producto por ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("Producto no encontrado"));
        return productoMapper.toResponse(producto);
    }

    //Crear nuevo producto
    public ProductoResponse crearProducto(ProductoRequest request){
        log.info("Creando producto con nombre: {}", request.getNombrep());
        if(productoRepository.existsByNombrep(request.getNombrep())){
            throw new ConflictException("El nombre del producto ya existe");
        }
        Producto producto = productoMapper.fromRequest(request);
        producto.setActivo(true);
        producto = productoRepository.save(producto);
        return productoMapper.toResponse(producto);

    }

    //Actualiza datos de un producto
    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) {
        log.info("Actualizando producto con ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado"));

        if (productoRepository.existsByNombrepAndIdNot(request.getNombrep(), id)) {
            throw new ConflictException("Ya existe otro producto con ese nombre");
        }
        if (request.getNombrep() != null) producto.setNombrep(request.getNombrep());
        if (request.getDescripcion() != null) producto.setDescripcion(request.getDescripcion());
        if (request.getPrecio() != null) producto.setPrecio(request.getPrecio());
        if (request.getStock() != null) producto.setStock(request.getStock());
        if (request.getCategoria() != null) producto.setCategoria(request.getCategoria());

        producto = productoRepository.save(producto);
        return productoMapper.toResponse(producto);
    }

   //Eliminacion de un producto por id
    public void eliminarProducto(Long id) {
            log.info("Eliminando producto con ID: {}", id);
            if (!productoRepository.existsById(id)) {
                throw new NoSuchElementException("Producto no encontrado");
            }
            productoRepository.deleteById(id);
        }   















}
