package cl.duoc.ordenes.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.ordenes.dto.UsuarioResponse;
import lombok.extern.slf4j.Slf4j; 

@Component
@Slf4j
public class usuarioClient {

    @Autowired
    private WebClient webClient;

    public UsuarioResponse buscarOrdenPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/ordenes/{id}", id)
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al comunicarse con el servicio de usuarios");
            return null;
        }
    }
}