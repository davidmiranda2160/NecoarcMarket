package cl.duoc.carrito.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Double obtenerMontoTotal(Long idUsuario) {
        try {
            return webClient.get()
                    .uri("/api/carrito/total/{idUsuario}", idUsuario)
                    .retrieve()
                    .bodyToMono(Double.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al conectar con el microservicio de Carrito: {}", e.getMessage());
            return 0.0;
        }
    }
}