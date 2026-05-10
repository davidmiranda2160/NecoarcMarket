package cl.duoc.productos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class WebClientConfig {

    @Value("${services.inventario.baseUrl}")
    private String inventarioBaseUrl;

    @Bean
    public WebClient webClient() {
    
        return WebClient.builder()
                .baseUrl(inventarioBaseUrl)
                .build();
    }
}
