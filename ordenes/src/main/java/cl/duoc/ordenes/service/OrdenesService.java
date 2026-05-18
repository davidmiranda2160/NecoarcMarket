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
            log.warn("Intento de creacion de orden fallido: El carrito del usuario con id {} está vacio", request.getUsuarioId());
            throw new NoSuchElementException("No hay productos en el carrito para generar una orden.");
        }

        BigDecimal totalCalculado = carrito.stream()
                .map(CarritoResponse::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Carrito recuperado con {} productos y monto total calculado: ${}", carrito.size(), totalCalculado);
                
        Ordenes orden = ordenesMapper.fromRequest(request, totalCalculado); 
        Ordenes ordenGuardada = ordenesRepository.save(orden);
        log.info("Orden guardada localmente con id: {}", ordenGuardada.getId());

        PagosRequest pagoReq = PagosRequest.builder()
                .idOrden(ordenGuardada.getId())
                .metodoPago(request.getMetodoPago())
                .montoAPagar(totalCalculado)
                .build();
        
        log.info("Enviando solicitud de pago al microservicio externo para la orden ID: {}", ordenGuardada.getId());
        pagosClient.procesarPago(pagoReq); 
        log.info("Pago procesado exitosamente para la orden con id: {}", ordenGuardada.getId());

        List<CarritoResponse> detalleParaRespuesta = new ArrayList<>(carrito); 

        log.info("Se esta solicitando limpieza de carrito para el usuario con id: {}", request.getUsuarioId());
        carritoClient.limpiarCarrito(request.getUsuarioId());
        log.info("Ls orden con id {} fue completada con éxito", ordenGuardada.getId());

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
        log.info("Buscando ordenes por estado: {}", estado);
        List<Ordenes> ordenes = ordenesRepository.findByEstadoOrden(estado);
        
        log.info("Se encontraron {} ordenes bajo el estado '{}'", ordenes.size(), estado);
        return ordenes.stream()
                .map(orden -> ordenesMapper.toResponse(orden, null))
                .collect(Collectors.toList());
    }

    public List<OrdenesResponse> obtenerTodas() {
        log.info("Buscando todas las ordenes del sistema");
        List<Ordenes> ordenes = ordenesRepository.findAll();
        
        log.info("Todas las ordenes fueron recuperadas de la base de datos: {}", ordenes.size());
        return ordenes.stream()
                .map(orden -> ordenesMapper.toResponse(orden, null))
                .collect(Collectors.toList());
    }

    public OrdenesResponse obtenerPorId(Long id) {
        log.info("Buscando orden por el id: {}", id);
        
        Ordenes orden = ordenesRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("La consulta fallo: La orden con el id {} no existe", id);
                    return new NoSuchElementException("Orden no encontrada con ID: " + id);
                });
        
        log.info("Orden con id {} encontrada. Consultando articulos en el carrito del usuario con id: {}", id, orden.getUsuarioId());
        List<CarritoResponse> items = carritoClient.obtenerCarritoPorUsuario(orden.getUsuarioId());

        return ordenesMapper.toResponse(orden, items);
    }
}
