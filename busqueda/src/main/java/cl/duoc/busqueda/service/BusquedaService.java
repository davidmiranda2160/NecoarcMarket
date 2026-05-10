package cl.duoc.busqueda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.busqueda.client.EnvioClient;
import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.dto.EnvioDTO;

@Service
public class BusquedaService {
   /*  @Autowired
    private EnvioClient enviosClient;

    public BusquedaResponse obtenerSeguimientoCompleto(String codigo) {
        // Llamada al micro de david plox apurate
        EnvioDTO envio = enviosClient.consultarEstado(codigo); //Revisar esta parte
        
        //logica de respuesta, pero debo esperar a david
        BusquedaResponse res = new BusquedaResponse();
        res.setCodigoSeguimiento(codigo);
        res.setEstado(envio.getEstado()); //Cuando termine el envioDTO que debe ser el mismo que el de david
        
        return res;
    }
    */
}
