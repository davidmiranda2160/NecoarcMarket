package cl.duoc.envio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {
    /*
    Este apartado es el encarga de hacer la comunicacion
    con el servicio que se quiere conectar
    */

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .build();
    }

}
