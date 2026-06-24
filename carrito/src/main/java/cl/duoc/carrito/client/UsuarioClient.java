package cl.duoc.carrito.client;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.carrito.dto.UsuarioResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder webClientBuilder,
            @Value("${service.usuario.baseUrl}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public UsuarioResponse obtenerUsuario(Long id) {
        try {
            return this.webClient.get()
                    .uri("/v1/usuario/{id}", id)
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error al conectar con el microservicio de usuarios para el id: {}", id);

            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new NoSuchElementException("No se encontró el usuario con id: " + id);
            } else if (ex.getStatusCode().is4xxClientError()) {
                throw new IllegalArgumentException("La petición enviada al servicio de usuarios es inválida.");
            } else {
                throw new RuntimeException("Error interno en el servicio de usuarios remoto al buscar ID: " + id);
            }
        }
    }
}
