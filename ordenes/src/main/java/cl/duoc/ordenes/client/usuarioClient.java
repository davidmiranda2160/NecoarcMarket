package cl.duoc.ordenes.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.ordenes.dto.UsuarioResponse;
import lombok.extern.slf4j.Slf4j; 

@Component
@Slf4j
public class UsuarioClient {

    @Autowired
    private WebClient webClient;

    @Value("${services.usuario.baseUrl}$")
    private String usuarioUrl;

    public UsuarioResponse obtenerUsuarioPorId(Long id){
        try {
            return webClient.get()
                    .uri(usuarioUrl + "/{id}", id)
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al comunicarse con el servicio de usuarios");
            return null;
        }
    }
}