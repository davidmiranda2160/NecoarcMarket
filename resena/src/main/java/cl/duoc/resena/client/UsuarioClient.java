package cl.duoc.resena.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import cl.duoc.resena.dto.UsuarioResponse; 

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient webClient) {
        this.webClient = webClient;
    }
    public UsuarioResponse obtenerDatosUsuario(Long id) {
        try {
            return webClient.get()
                    .uri("/v1/usuario/{id}", id) 
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block(); 
        } catch (Exception e) {
            log.error("Error al llamar al microservicio de usuarios para el ID: {}", id, e);
            UsuarioResponse usuarioResponse = new UsuarioResponse();
            usuarioResponse.setNombre("Servicio de Usuarios no disponible");
            usuarioResponse.setApellidos(""); 
            return usuarioResponse;
        }
    }
}
