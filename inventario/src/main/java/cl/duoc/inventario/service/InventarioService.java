package cl.duoc.inventario.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.inventario.client.ProductoClient;
import cl.duoc.inventario.dto.InventarioResponse;
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
        log.info("Consultando inventario para el producto ID: {}", productoId);
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(()-> new NoSuchElementException("El producto no tiene registro en el inventario"));
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

}
