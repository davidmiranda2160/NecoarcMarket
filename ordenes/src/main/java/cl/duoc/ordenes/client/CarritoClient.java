package cl.duoc.ordenes.client;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.ordenes.dto.CarritoResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CarritoClient {

    private final WebClient webClient;

    public CarritoClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8087/v1/carrito").build();
    }

    public List<CarritoResponse> obtenerCarritoPorUsuario(Long idUsuario) {
        log.info("Obteniendo carrito del usuario {}", idUsuario);
        return webClient.get()
                .uri("/usuario/{idUsuario}", idUsuario)
                .retrieve()
                .bodyToFlux(CarritoResponse.class)
                .collectList()
                .block();
    }

    public void limpiarCarrito(Long usuarioId) {
        try {
            log.info("Solicitando limpieza de carrito para el usuario: {}", usuarioId);
            this.webClient.delete()
                    .uri("/usuario/{idUsuario}", usuarioId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("Carrito del usuario {} limpiado con éxito", usuarioId);
        } catch (Exception e) {
            log.error("No se pudo limpiar el carrito del usuario {}: {}", usuarioId, e.getMessage());
        }
    }
}
