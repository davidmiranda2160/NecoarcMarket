package cl.duoc.resena.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import cl.duoc.resena.dto.UsuarioResponse; 

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder builder, 
                         @Value("${services.usuario.url}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }

    public UsuarioResponse obtenerDatosUsuario(Long id) {
        return this.webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(UsuarioResponse.class)
                .block(); 
    }
}
