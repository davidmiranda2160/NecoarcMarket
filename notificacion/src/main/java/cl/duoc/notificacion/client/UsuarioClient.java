package cl.duoc.notificacion.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.notificacion.dto.UsuarioResponse;

@Component
public class UsuarioClient {
    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder builder, @Value("${services.usuario.url}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }
    public UsuarioResponse obtenerDatosUsuario(Long id) {
        try {
            return this.webClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block();
        } catch (Exception e) {
            return null; 
        }
    }
}
