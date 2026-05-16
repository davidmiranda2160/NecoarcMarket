package cl.duoc.busqueda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.busqueda.client.EnvioClient;
import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.dto.EnvioResponse;
import cl.duoc.busqueda.model.Busqueda;
import cl.duoc.busqueda.repository.BusquedaRepository;

@Service
public class BusquedaService {
    @Autowired
    private EnvioClient enviosClient;

    @Autowired
    private BusquedaRepository busquedaRepository;

   /* public BusquedaResponse obtenerSeguimientoCompleto(String codigo) {
        // Llamada al micro de david plox apurate
        EnvioResponse envio = enviosClient.consultarEstado(codigo); //Revisar esta parte
        
        //logica de respuesta, pero debo esperar a david
        BusquedaResponse res = new BusquedaResponse();
        res.setCodigoSeguimiento(codigo);
        res.setEstadoEnvio(envio.getEstadoEnvio());
        
        return res;
    }*/
    //Nota importante, esperar a que david finalice Envio para poder arreglar EnvioCliente de Busqueda, así reparo BusquedaService

    public BusquedaResponse obtenerSeguimientoCompleto(String codigo) {
        Busqueda busquedaLocal = busquedaRepository.findByCodigoSeguimiento(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "El código de seguimiento '" + codigo + "' no está registrado en NecoarcMarket."));

        Long envioId = busquedaLocal.getEnvioId();
        EnvioResponse envioExterno = enviosClient.consultarEstado(envioId); 
        BusquedaResponse res = new BusquedaResponse();
        res.setCodigoSeguimiento(codigo);
        if (envioExterno != null) {
            res.setEstadoEnvio(envioExterno.getEstadoEnvio());
                        if (!busquedaLocal.getEstadoEnvio().equals(envioExterno.getEstadoEnvio())) {
                busquedaLocal.setEstadoEnvio(envioExterno.getEstadoEnvio());
                busquedaRepository.save(busquedaLocal);
            }
        } else {
            res.setEstadoEnvio(busquedaLocal.getEstadoEnvio());
        }
        
        return res;
    }
}


