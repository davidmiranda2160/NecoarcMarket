package cl.duoc.envio.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.duoc.envio.client.OrdenesClient;
import cl.duoc.envio.dto.EnvioRequest;
import cl.duoc.envio.dto.EnvioResponse;
import cl.duoc.envio.dto.OrdenesResponse;
import cl.duoc.envio.mapper.EnvioMapper;
import cl.duoc.envio.model.Envio;
import cl.duoc.envio.repository.EnvioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final EnvioMapper envioMapper;
    private final OrdenesClient ordenesClient;

    public EnvioResponse crearEnvio(EnvioRequest request) {
    Long ordenId = request.getOrdenId();
    
    OrdenesResponse orden = ordenesClient.buscarOrdenPorId(ordenId);
    if (orden == null) {
        log.warn("No se pudo crear el envio: la orden con id {} no existe en el sistema", ordenId);
        throw new NoSuchElementException("No se encontró la orden con id: " + ordenId); 
    }
    
    Envio envio = envioMapper.fromRequest(request);
    
    envio.setOrdenId(ordenId);
    envio.setUsuarioId(request.getUsuarioId());
    envio.setEstadoEnvio("Pendiente"); 
    
    String codigoSeguimiento = String.format("TRACK-NECO-%03d", ordenId);
    envio.setCodigoSeguimiento(codigoSeguimiento); 
    
    envio.setFechaCreacion(LocalDateTime.now());
    envio.setFechaEstimadaEntrega(LocalDate.now().plusDays(3)); 

    Envio guardado = envioRepository.save(envio);
    log.info("El envio fue creado exitosamente. id generado: {} - Tracking: {}", guardado.getId(), guardado.getCodigoSeguimiento());
    return envioMapper.toResponse(guardado, orden);
}

    public List<EnvioResponse> listarTodos() {
        log.info("Solicitando el listado de todos los envíos");
        List<Envio> envios = envioRepository.findAll();
        
        log.info("Se encontraron {} envios en la base de datos.", envios.size());
        
        return envios.stream()
                .map(envio -> {
                    OrdenesResponse orden = ordenesClient.buscarOrdenPorId(envio.getOrdenId());
                    return envioMapper.toResponse(envio, orden);
                })
                .collect(Collectors.toList());
    }

    public EnvioResponse obtenerPorId(Long id) {
        log.info("Buscando envio con id: {}", id);
        
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Consulta fallida: el envío con id {} no existe", id);
                    return new NoSuchElementException("No se encontró el envío solicitado con id: " + id);
                });

        OrdenesResponse orden = ordenesClient.buscarOrdenPorId(envio.getOrdenId());
        return envioMapper.toResponse(envio, orden);
    }

    public EnvioResponse actualizarEstado(Long id, String nuevoEstado) {
        log.info("Solicitud para actualizar el estado del envío ID {} a: '{}'", id, nuevoEstado);

        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            log.warn("Actualización rechazada: El nuevo estado enviado está vacio o es nulo");
            throw new IllegalArgumentException("El nuevo estado del envio no puede estar vacío.");
        }

        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Actualización fallida: En el envío con id {} no existe", id);
                    return new NoSuchElementException("No se puede actualizar el estado porque no existe el envio con id: " + id);
                });

        envio.setEstadoEnvio(nuevoEstado.toUpperCase().trim());
        Envio actualizado = envioRepository.save(envio);
        log.info("Estado del envio id {} actualizado con exito a '{}'", id, nuevoEstado);

        OrdenesResponse orden = ordenesClient.buscarOrdenPorId(actualizado.getOrdenId());
        return envioMapper.toResponse(actualizado, orden);
    }
}