package cl.duoc.busqueda.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/*@Component
public class EnvioClient {
    private final WebClient webClient;

    public EnviosClient(WebClient.Builder webClientBuilder, 
                        @Value("${services.envios.url}") String url) {
        this.webClient = webClientBuilder.baseUrl(url).build();
    }

    public EnvioDTO consultarEstado(String codigo) {
        return this.webClient.get()
                .uri("/rastrear/{codigo}", codigo)
                .retrieve()
                .bodyToMono(EnvioDTO.class)
                .block(); 
    }
             
} */   
