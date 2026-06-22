package cl.duoc.notificacion.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.notificacion.dto.UsuarioResponse;
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
        } catch (WebClientResponseException ex) {
            log.error("Error HTTP al llamar al micro de Usuarios para el ID {}: {} - {}", 
                      id, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw switch (ex.getStatusCode().value()) {
                case 401 -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                        "No autorizado para acceder al micro de usuarios.");
                case 403 -> new ResponseStatusException(HttpStatus.FORBIDDEN, 
                        "Acceso prohibido: revisa la configuración de seguridad de usuario");
                case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "El usuario con ID " + id + " no existe en el sistema.");
                default -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                        "Error inesperado en el micro de usuarios: " + ex.getMessage());
            };
        } catch (Exception e) {
            log.error("Error de red o desconocido al conectar con usuarios: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, 
                    "El servicio de usuarios no está disponible actualmente.");
        }
    }
    
    public String getNombreById(Long id) {
        try {
            UsuarioResponse user = obtenerDatosUsuario(id);
            if (user != null) {
                return user.getNombre() + " " + user.getApellidos();
            }
        } catch (Exception e) {
            log.warn("Usando nombre de respaldo para ID {} debido a un error en el cliente", id);
        }
        return "Usuario " + id;
    }
}
