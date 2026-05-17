package cl.duoc.ordenes.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.ordenes.client.CarritoClient;
import cl.duoc.ordenes.client.PagosClient;
import cl.duoc.ordenes.dto.CarritoResponse;
import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.dto.PagosRequest;
import cl.duoc.ordenes.mapper.OrdenesMapper;
import cl.duoc.ordenes.model.Ordenes;
import cl.duoc.ordenes.repository.OrdenesRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class OrdenesService {
    @Autowired
    private OrdenesRepository ordenesRepository;

    @Autowired
    private OrdenesMapper ordenesMapper;

    @Autowired
    private CarritoClient carritoClient;

    @Autowired
    private PagosClient pagosClient;

    public OrdenesResponse crearOrden(OrdenesRequest request) {
        log.info("Iniciando creación de orden para usuario ID: {}", request.getUsuarioId());

        List<CarritoResponse> carrito = carritoClient.obtenerCarritoPorUsuario(request.getUsuarioId());

        if (carrito == null || carrito.isEmpty()) {
            throw new NoSuchElementException("No hay productos en el carrito para generar una orden.");
        }

        BigDecimal totalCalculado = carrito.stream()
                .map(CarritoResponse::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        Ordenes orden = ordenesMapper.fromRequest(request, totalCalculado); 
        Ordenes ordenGuardada = ordenesRepository.save(orden);
        log.info("Orden guardada localmente con ID: {}", ordenGuardada.getId());

        PagosRequest pagoReq = PagosRequest.builder()
                .idOrden(ordenGuardada.getId())
                .metodoPago(request.getMetodoPago())
                .montoAPagar(totalCalculado)
                .build();
        
        pagosClient.procesarPago(pagoReq); 

        List<CarritoResponse> detalleParaRespuesta = new ArrayList<>(carrito); 

        carritoClient.limpiarCarrito(request.getUsuarioId());

        return ordenesMapper.toResponse(ordenGuardada, detalleParaRespuesta);
    }

    public List<OrdenesResponse> obtenerPorUsuario(Long usuarioId) {
        log.info("Buscando historial para el usuario: {}", usuarioId);
        
        List<CarritoResponse> items = null;
        try {
            items = carritoClient.obtenerCarritoPorUsuario(usuarioId);
        } catch (Exception e) {
            log.error("No se pudo obtener el carrito para el historial del usuario {}", usuarioId);
        }

        List<CarritoResponse> finalItems = items; 
        
        return ordenesRepository.findByUsuarioId(usuarioId).stream()
                .map(orden -> ordenesMapper.toResponse(orden, finalItems))
                .collect(Collectors.toList());
    }

    public List<OrdenesResponse> obtenerPorEstado(String estado) {
        log.info("Buscando órdenes por estado: {}", estado);
        return ordenesRepository.findByEstadoOrden(estado).stream()
                .map(orden -> ordenesMapper.toResponse(orden, null))
                .collect(Collectors.toList());
    }

    public List<OrdenesResponse> obtenerTodas() {
        log.info("Buscando todas las órdenes del sistema");
        return ordenesRepository.findAll().stream()
                .map(orden -> ordenesMapper.toResponse(orden, null))
                .collect(Collectors.toList());
    }

    public OrdenesResponse obtenerPorId(Long id) {
        log.info("Buscando orden por ID: {}", id);
        Ordenes orden = ordenesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Orden no encontrada con ID: " + id));
        
        List<CarritoResponse> items = null;
        try {
            items = carritoClient.obtenerCarritoPorUsuario(orden.getUsuarioId());
        } catch (Exception e) {
            log.error("No se pudo obtener el detalle del carrito para la orden {}", id);
        }

        return ordenesMapper.toResponse(orden, items);
    }
}
