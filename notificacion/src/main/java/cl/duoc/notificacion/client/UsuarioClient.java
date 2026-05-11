package cl.duoc.notificacion.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.notificacion.dto.UsuarioResponse;

@Component
public class UsuarioClient {
    private final WebClient webClient;

    public UsuarioClient(WebClient webClient) {
        this.webClient = webClient;
    }
    public UsuarioResponse obtenerDatosUsuario(Long id) {
    try {
        return webClient.get()
                .uri("/v1/usuarios/{id}", id)
                .retrieve()
                .bodyToMono(UsuarioResponse.class)
                .block();
    } catch (Exception e) {
        System.err.println("Error al llamar al micro de David: " + e.getMessage());
        e.printStackTrace(); 
        return null; 
    }
}
    
    public String getNombreById(Long id) {
        UsuarioResponse user = obtenerDatosUsuario(id);
        if (user != null) {
            return user.getNombre() + " " + user.getApellidos();
        }
        return "Usuario " + id;
    }
}
