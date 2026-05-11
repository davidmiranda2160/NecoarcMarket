package cl.duoc.envio.service;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.envio.client.OrdenesClient;
import cl.duoc.envio.dto.EnvioRequest;
import cl.duoc.envio.dto.EnvioResponse;
import cl.duoc.envio.dto.OrdenesResponse;
import cl.duoc.envio.mapper.EnvioMapper;
import cl.duoc.envio.model.Envio;
import cl.duoc.envio.repository.EnvioRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private EnvioMapper envioMapper;

    @Autowired
    private OrdenesClient ordenesClient;

    public EnvioResponse crearEnvio(EnvioRequest request, Long ordenId) {
        OrdenesResponse orden = ordenesClient.buscarOrdenPorId(ordenId);
        if (orden == null) {
            log.error("No se encontró la orden con id: ", ordenId);
            return null; 
        }
        
        Envio envio = envioMapper.fromRequest(request);
        
        envio.setOrdenId(ordenId);
        envio.setEstadoEnvio("Realizado");
        envio.setNumeroSeguimiento("ENV-" + ordenId); //El numero de seguimiento se construye con el id del cliente
        envio.setFechaEstimadaEntrega(LocalDate.now());

        Envio guardado = envioRepository.save(envio);
        return envioMapper.toResponse(guardado, orden);
    }

    public List<EnvioResponse> listarTodos() {
        return envioRepository.findAll().stream()
                .map(envio -> {
                    OrdenesResponse orden = ordenesClient.buscarOrdenPorId(envio.getOrdenId());
                    return envioMapper.toResponse(envio, orden);
                })
                .collect(Collectors.toList());
    }

    public EnvioResponse obtenerPorId(Long id) {
        return envioRepository.findById(id).map(envio -> {
            OrdenesResponse orden = ordenesClient.buscarOrdenPorId(envio.getOrdenId());
            return envioMapper.toResponse(envio, orden);
        }).orElse(null);
    }

    public EnvioResponse actualizarEstado(Long id, String nuevoEstado) {
        return envioRepository.findById(id).map(envio -> {
            envio.setEstadoEnvio(nuevoEstado);
            Envio actualizado = envioRepository.save(envio);
            OrdenesResponse orden = ordenesClient.buscarOrdenPorId(envio.getOrdenId());
            return envioMapper.toResponse(actualizado, orden);
        }).orElse(null);
    }

    
}

