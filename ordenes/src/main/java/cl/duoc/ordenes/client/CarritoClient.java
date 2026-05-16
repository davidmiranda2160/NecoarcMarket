package cl.duoc.ordenes.client;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.ordenes.dto.CarritoResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CarritoClient {

    private final WebClient webClient;

    public CarritoClient(WebClient.Builder builder, @Value("${services.carrito.baseUrl}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public CarritoResponse buscarCarritoPorUsuarioId(Long idUsuario) {
        try {
            return webClient.get()
                    .uri("/v1/carrito/usuario/{id}", idUsuario)
                    .retrieve()
                    .bodyToMono(CarritoResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new NoSuchElementException("El carrito del usuario " + idUsuario + " está vacío o no existe.");
            }
            throw new RuntimeException("Error en el servicio de  carrito: " + ex.getMessage());
        } catch (Exception e) {
            log.error("El servicio de Carrito no disponible: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servicio de Carrito.");
        }
    }

    public void vaciarCarrito(Long idUsuario) {
        try {
            webClient.delete()
                .uri("/v1/carrito/usuario/{usuarioId}", idUsuario)
                .retrieve()
                .toBodilessEntity()
                .block();
        } catch (Exception e) {
            log.error("No se pudo vaciar el carrito del usuario {}", idUsuario);
        }
    }
}