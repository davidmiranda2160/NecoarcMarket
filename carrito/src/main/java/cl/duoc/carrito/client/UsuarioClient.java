package cl.duoc.carrito.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.carrito.dto.UsuarioResponse;

@Component
public class UsuarioClient {

   private final WebClient webClient;
    

    public UsuarioClient(WebClient.Builder webClientBuilder, 
        @Value("${services.usuario.baseUrl}") String baseUrl){
            this.webClient = webClientBuilder.baseUrl(baseUrl)
            .build();
        }

    public UsuarioResponse obtenerUsuario(Long id){
        return this.webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(UsuarioResponse.class)
                .block();
    }    
}