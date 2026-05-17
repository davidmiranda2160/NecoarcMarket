package cl.duoc.ordenes.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.ordenes.client.CarritoClient;
import cl.duoc.ordenes.dto.CarritoResponse;
import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.mapper.OrdenesMapper;
import cl.duoc.ordenes.model.Ordenes;
import cl.duoc.ordenes.repository.OrdenesRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenesService {
    @Autowired
    private OrdenesRepository ordenesRepository;

    @Autowired
    private OrdenesMapper ordenesMapper;

    @Autowired
    private CarritoClient carritoClient;

    public OrdenesResponse crearOrden(OrdenesRequest request) {
        log.info("Iniciando creación de orden para usuario ID: {}", request.getUsuarioId());

        List<CarritoResponse> carrito = carritoClient.obtenerCarritoPorUsuario(request.getUsuarioId());

        if (carrito == null || carrito.isEmpty()) {
            throw new NoSuchElementException("No hay productos en el carrito para generar una orden.");
        }

        BigDecimal totalCalculado = carrito.stream()
                .map(CarritoResponse::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Total recuperado del carrito: {}", totalCalculado);

        Ordenes orden = ordenesMapper.fromRequest(request, totalCalculado); 
        
        Ordenes ordenGuardada = ordenesRepository.save(orden);
        log.info("Orden guardada con éxito. ID: {}", ordenGuardada.getId());

        return ordenesMapper.toResponse(ordenGuardada);
    }

    public List<OrdenesResponse> obtenerTodas() {
        return ordenesRepository.findAll().stream()
                .map(ordenesMapper::toResponse)
                .collect(Collectors.toList());
    }

    public OrdenesResponse obtenerPorId(Long id) {
        Ordenes orden = ordenesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Orden no encontrada con ID: " + id));
        return ordenesMapper.toResponse(orden);
    }

    public OrdenesResponse actualizarEstado(Long id, String nuevoEstado) {
        Ordenes orden = ordenesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se pudo actualizar: Orden no encontrada."));
        
        orden.setEstadoOrden(nuevoEstado);
        return ordenesMapper.toResponse(ordenesRepository.save(orden));
    }

    public void eliminarOrden(Long id) {
        if (!ordenesRepository.existsById(id)) {
            throw new NoSuchElementException("No se pudo eliminar: Orden no existe.");
        }
        ordenesRepository.deleteById(id);
    }
}
