package cl.duoc.busqueda.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.busqueda.client.EnvioClient;
import cl.duoc.busqueda.dto.BusquedaRequest;
import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.dto.EnvioResponse;
import cl.duoc.busqueda.mapper.BusquedaMapper;
import cl.duoc.busqueda.model.Busqueda;
import cl.duoc.busqueda.repository.BusquedaRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BusquedaService {
    @Autowired
    private BusquedaRepository busquedaRepository;

    @Autowired
    private EnvioClient envioClient;

    @Autowired
    private BusquedaMapper busquedaMapper;

    public BusquedaResponse obtenerSeguimientoCompleto(String codigo) {
        log.info("Iniciando búsqueda de seguimiento para el código: {}", codigo);
        Busqueda busquedaLocal = busquedaRepository.findByCodigoSeguimiento(codigo)
                .orElseThrow(() -> {
                    log.warn("Fallo en búsqueda: El código de seguimiento '{}' no está registrado.", codigo);
                    return new NoSuchElementException("El código de seguimiento '" + codigo + "' no está registrado.");
                });
        String estadoFinal = busquedaLocal.getEstadoEnvio(); 

        try {
            EnvioResponse envioExterno = envioClient.consultarEstado(busquedaLocal.getEnvioId());   //Debo añadir también coherencia con el código de seguimiento de david vs el mío
            if (envioExterno != null && envioExterno.getEstadoEnvio() != null) {
                estadoFinal = envioExterno.getEstadoEnvio(); 
                busquedaLocal.setEstadoEnvio(estadoFinal);
                busquedaRepository.save(busquedaLocal);
            }
        } catch (Exception e) {
            log.warn("No se pudo conectar con Envíos. Usando estado local de respaldo: {}", e.getMessage());
        }
        return busquedaMapper.toResponse(busquedaLocal, estadoFinal);
    }
    public BusquedaResponse registrarNuevoSeguimiento(BusquedaRequest request) {
        log.info("Registrando nuevo código en el sistema: {}", request.getCodigoSeguimiento());
        Busqueda nuevaBusqueda = busquedaMapper.fromRequest(request);

        Busqueda guardada = busquedaRepository.save(nuevaBusqueda);

        return busquedaMapper.toResponse(guardada, request.getEstadoEnvio());
    }
}


