package cl.duoc.ordenes.client;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.ordenes.dto.UsuarioResponse;
import lombok.extern.slf4j.Slf4j; 

@Component
@Slf4j
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder builder, @Value("${services.usuario.baseUrl}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public UsuarioResponse obtenerUsuarioPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/v1/usuario/{id}", id)
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new NoSuchElementException("Usuario con ID " + id + " no encontrado.");
            }
            throw new RuntimeException("Error en servicio de usuarios");
        }
    }
}
