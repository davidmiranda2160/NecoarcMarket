package cl.duoc.envio.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.envio.dto.OrdenesResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrdenesClient {

    
    private final WebClient webClient;


    public OrdenesClient(WebClient.Builder webClientBuilder,
            @Value("${services.ordenes.baseUrl}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public OrdenesResponse buscarOrdenPorId(Long id) {
        try {
            return webClient.get()
                    .uri("v1/ordenes/{id}", id)
                    .retrieve()
                    .bodyToMono(OrdenesResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al comunicarse con el servicio de ordenes");
            return null;
        }
    }
}
