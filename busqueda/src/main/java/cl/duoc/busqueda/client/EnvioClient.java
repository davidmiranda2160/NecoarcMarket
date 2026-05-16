package cl.duoc.busqueda.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.busqueda.dto.EnvioResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EnvioClient {
    private WebClient webClient;

    public EnvioClient(WebClient webClient) {
        this.webClient = webClient;
    }
    public EnvioResponse consultarEstado(Long id) { 
        try {
            return this.webClient.get()
                    .uri("/envio/{id}", id) //David, en que quedamos con usar la /v1
                    .retrieve()
                    .bodyToMono(EnvioResponse.class)
                    .block(); 
        } catch (WebClientResponseException ex) {
            log.error("Error HTTP al consultar envío ID {}: David respondió con {}", id, ex.getStatusCode());
            
            if (ex.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "El envío con ID '" + id + "' no existe en el sistema de despachos.");
            }
            throw new ResponseStatusException(ex.getStatusCode(), "Error en el servicio de envíos.");
            
        } catch (Exception e) {
            log.error("Fallo de conexión con el micro de Envíos al buscar ID {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, 
                "El servicio de despacho de Neco-Arc no está disponible.");
        }
    }
             
}    
