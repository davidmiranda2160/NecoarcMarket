package cl.duoc.busqueda.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.busqueda.dto.EnvioResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EnvioClient {
    private WebClient webClient;

    public EnvioClient(WebClient.Builder webClientBuilder, 
                        @Value("${services.envio.url}") String url) {
        this.webClient = webClientBuilder.baseUrl(url).build();
    }

    public EnvioResponse consultarEstado(String codigo) {
        return this.webClient.get()
                .uri("/rastrear/{codigo}", codigo)
                .retrieve()
                .bodyToMono(EnvioResponse.class)
                .block(); 
    }
             
}    
