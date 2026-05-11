package cl.duoc.busqueda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.busqueda.client.EnvioClient;
import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.dto.EnvioResponse;

@Service
public class BusquedaService {
    @Autowired
    private EnvioClient enviosClient;

    public BusquedaResponse obtenerSeguimientoCompleto(String codigo) {
        // Llamada al micro de david plox apurate
        EnvioResponse envio = enviosClient.consultarEstado(codigo); //Revisar esta parte
        
        //logica de respuesta, pero debo esperar a david
        BusquedaResponse res = new BusquedaResponse();
        res.setCodigoSeguimiento(codigo);
        res.setEstadoEnvio(envio.getEstadoEnvio());
        
        return res;
    }
    //Nota importante, esperar a que david finalice Envio para poder arreglar EnvioCliente de Busqueda, así reparo BusquedaService
}
