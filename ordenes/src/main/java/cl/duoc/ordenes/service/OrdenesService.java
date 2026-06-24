package cl.duoc.ordenes.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.duoc.ordenes.client.CarritoClient;
import cl.duoc.ordenes.client.InventarioClient;
import cl.duoc.ordenes.dto.CarritoResponse;
import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.mapper.OrdenesMapper;
import cl.duoc.ordenes.model.Ordenes;
import cl.duoc.ordenes.model.OrdenesDetalle;
import cl.duoc.ordenes.repository.OrdenesDetalleRepository;
import cl.duoc.ordenes.repository.OrdenesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class OrdenesService {

    private final OrdenesRepository ordenesRepository;

    private final OrdenesDetalleRepository ordenesDetalleRepository;

    private final OrdenesMapper ordenesMapper;

    private final CarritoClient carritoClient;

    private final InventarioClient inventarioClient;

    public OrdenesResponse crearOrden(OrdenesRequest request) {
        log.info("Iniciando creación de orden descentralizada para usuario ID: {}", request.getUsuarioId());

        List<CarritoResponse> carrito;
        try {
            // 1. Intentamos obtener el carrito de forma normal
            carrito = carritoClient.obtenerCarritoPorUsuario(request.getUsuarioId());
        } catch (Exception ex) {
            
            log.warn("No se pudo obtener el carrito del usuario {}. Se asumirá como vacío. Motivo: {}", 
                    request.getUsuarioId(), ex.getMessage());
            carrito = List.of(); 
        }

        if (carrito == null || carrito.isEmpty()) {
            throw new NoSuchElementException("No hay productos en el carrito para generar una orden.");
        }

        BigDecimal totalCalculado = carrito.stream()
                .map(CarritoResponse::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Verificando stock y comprometiendo existencias en inventario...");
        for (CarritoResponse item : carrito) {
            inventarioClient.descontarStock(item.getProducto().getId(), item.getCantidad());
        }

        Ordenes orden = ordenesMapper.fromRequest(request, totalCalculado);
        Ordenes ordenGuardada = ordenesRepository.save(orden);
        log.info("Orden de cabecera guardada exitosamente con ID: {}", ordenGuardada.getId());

        log.info("Generando instantánea histórica de detalles para la orden ID: {}", ordenGuardada.getId());
        List<OrdenesDetalle> detalles = carrito.stream()
                .map(item -> ordenesMapper.toDetalleEntity(item, ordenGuardada.getId()))
                .collect(Collectors.toList());

        List<OrdenesDetalle> detallesGuardados = ordenesDetalleRepository.saveAll(detalles);
        log.info("Se registrararon {} líneas de detalle en la base de datos.", detallesGuardados.size());

        log.info("Vaciando el carrito del usuario ID: {} tras conversión exitosa.", request.getUsuarioId());
        carritoClient.limpiarCarrito(request.getUsuarioId());

        return ordenesMapper.toResponse(ordenGuardada, detallesGuardados);
    }

    public OrdenesResponse obtenerPorId(Long id) {
        log.info("Consultando historial estático para la orden ID: {}", id);
        Ordenes orden = ordenesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Orden no encontrada con ID: " + id));

        List<OrdenesDetalle> detalles = ordenesDetalleRepository.findByOrdenId(orden.getId());

        return ordenesMapper.toResponse(orden, detalles);
    }

    public List<OrdenesResponse> obtenerPorUsuario(Long usuarioId) {
        log.info("Buscando lista de órdenes del usuario: {}", usuarioId);
        
        List<Ordenes> ordenes = ordenesRepository.findByUsuarioId(usuarioId);
        
        if (ordenes == null || ordenes.isEmpty()) {
            log.warn("No se encontraron ordenes registradas para el usuario con id: {}", usuarioId);
            throw new NoSuchElementException("No se encontraron ordenes para el usuario con id: " + usuarioId);
        }

        return ordenes.stream()
                .map(orden -> {
                    List<OrdenesDetalle> detalles = ordenesDetalleRepository.findByOrdenId(orden.getId());
                    return ordenesMapper.toResponse(orden, detalles);
                })
                .collect(Collectors.toList());
    }

    public List<OrdenesResponse> obtenerTodas() {
        log.info("Recuperando el catálogo completo de órdenes del sistema");
        return ordenesRepository.findAll().stream()
                .map(orden -> {
                    List<OrdenesDetalle> detalles = ordenesDetalleRepository.findByOrdenId(orden.getId());
                    return ordenesMapper.toResponse(orden, detalles);
                })
                .collect(Collectors.toList());
    }

   public List<OrdenesResponse> obtenerPorEstado(String estado) {
        log.info("Buscando ordenes por estado: {}", estado);
        
        List<Ordenes> ordenes = ordenesRepository.findByEstadoOrden(estado);
        
        if (ordenes == null || ordenes.isEmpty()) {
            log.warn("No se encontraron órdenes en el sistema con el estado: {}", estado);
            throw new NoSuchElementException("No se encontraron órdenes con el estado: " + estado);
        }

        return ordenes.stream()
                .map(orden -> {
                    List<OrdenesDetalle> detalles = ordenesDetalleRepository.findByOrdenId(orden.getId());
                    return ordenesMapper.toResponse(orden, detalles);
                })
                .collect(Collectors.toList());
    }
    public void actualizarEstadoDesdePagos(Long idOrden, String nuevoEstado) {
        log.info("Cambiando estado de orden ID {} a: {}", idOrden, nuevoEstado);

        Ordenes orden = ordenesRepository.findById(idOrden)
                .orElseThrow(() -> new NoSuchElementException("No existe la orden con ID: " + idOrden));

        if ("Cancelada".equalsIgnoreCase(nuevoEstado)) {
            log.warn("La orden ID {} fue rechazada en pagos. Iniciando proceso de devolución de stock...", idOrden);

            List<OrdenesDetalle> detalles = ordenesDetalleRepository.findByOrdenId(idOrden);

            for (OrdenesDetalle detalle : detalles) {
                log.info("Reintegrando stock: Producto ID {}, Cantidad {}", detalle.getIdProducto(), detalle.getCantidad());
        
                inventarioClient.reintegrarStock(detalle.getIdProducto(), detalle.getCantidad());
            }
        }

        orden.setEstadoOrden(nuevoEstado);
        ordenesRepository.save(orden);
        log.info("Estado de la orden ID {} actualizado exitosamente en base de datos.", idOrden);
    }
}
