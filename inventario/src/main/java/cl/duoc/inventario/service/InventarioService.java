package cl.duoc.inventario.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.inventario.client.ProductoClient;
import cl.duoc.inventario.dto.InventarioResponse;
import cl.duoc.inventario.dto.ProductoDetalleDTO;
import cl.duoc.inventario.dto.ProductoResponse;
import cl.duoc.inventario.exception.ConflictException;
import cl.duoc.inventario.mapper.InventarioMapper;
import cl.duoc.inventario.model.Inventario;
import cl.duoc.inventario.repository.InventarioRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InventarioService {
    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private InventarioMapper inventarioMapper;

    @Autowired
    private ProductoClient productoClient;


    public InventarioResponse obtenerStockPorProducto(Long productoId){
        log.info("Consultando stock plano en base de datos para el producto ID: {}", productoId);
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new NoSuchElementException("El producto no tiene registro en el inventario"));
        
        return inventarioMapper.toResponse(inventario);
    }

    public InventarioResponse agregarStock(Long productoId, Integer cantidad) {
        log.info("Validando existencia del producto {} antes de agregar stock", productoId);
        productoClient.obtenerProductoPorId(productoId);

        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseGet(() -> Inventario.builder().productoId(productoId).cantidad(0).build());
        inventario.setCantidad(inventario.getCantidad() + cantidad);
        inventario.setUltimaActualizacion(LocalDateTime.now());
        return inventarioMapper.toResponse(inventarioRepository.save(inventario));
    }

    public InventarioResponse descontarStock(Long productoId, Integer cantidadADescontar) {
        log.info("Descontando {} unidades al producto ID: {}", cantidadADescontar, productoId);

        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new NoSuchElementException("No existe inventario para este producto"));
        if (inventario.getCantidad() < cantidadADescontar) {
            log.warn("Fallo de stock insuficiente para el producto ID {}. Solicitado: {}, Disponible: {}", 
                     productoId, cantidadADescontar, inventario.getCantidad());
            throw new ConflictException("Stock insuficiente. Disponible: " + inventario.getCantidad());
        }
        inventario.setCantidad(inventario.getCantidad() - cantidadADescontar);
        inventario.setUltimaActualizacion(LocalDateTime.now());
        return inventarioMapper.toResponse(inventarioRepository.save(inventario));
    }

    public boolean tieneStockSuficiente(Long productoId, Integer cantidadRequerida) {
        return inventarioRepository.findByProductoId(productoId)
                .map(inv -> inv.getCantidad() >= cantidadRequerida)
                .orElse(false);
    }

    public ProductoDetalleDTO obtenerDetalleCompleto(Long id) {
        log.info("Cruce para obtener detalle completo del producto ID: {}", id);
        Inventario inv = inventarioRepository.findByProductoId(id)
                .orElseThrow(() -> {
                    log.warn("Fallo al obtener detalle del producto con ID {} no tiene registro en el inventario", id);
                    return new NoSuchElementException("El producto con id " + id + " no tiene registro en el inventario");
                });
        ProductoResponse prod = productoClient.obtenerProductoPorId(id);
        return inventarioMapper.toDetalleDTO(inv, prod);
    }

    //Metodo para eliminar de raiz el inventario por producto, toca testear
    public void eliminarInventarioPorProducto(Long productoId) {
        log.info("Eliminando por completo el registro de inventario para el producto ID: {}", productoId);
    
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> {
                    log.warn("Intento de eliminación fallido, l producto con ID {} no tiene registro en inventario", productoId);
                    return new NoSuchElementException("No se puede eliminar: el producto con ID " + productoId + " no tiene registro en inventario");
                });        
        inventarioRepository.delete(inventario);
    }


}
