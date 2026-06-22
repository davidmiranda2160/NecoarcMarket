package cl.duoc.resena.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.usuario.baseUrl}")
    private String usuariosApiUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder() 
                .baseUrl(usuariosApiUrl)
                .build();
    }
}
