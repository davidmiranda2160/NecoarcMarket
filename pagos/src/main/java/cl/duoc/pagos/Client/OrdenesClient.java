package cl.duoc.pagos.Client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrdenesClient {
    private final WebClient webClient;

    public OrdenesClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8088/v1/ordenes").build();
    }

    public void actualizarEstadoOrden(Long idOrden, String estado) {
        log.info("Enviando actualización a órdenes para el ID {}. Nuevo estado: {}", idOrden, estado);
        try {
            this.webClient.put()
                    .uri(uriBuilder -> uriBuilder
                    .path("/{idOrden}/estado")
                    .queryParam("estado", estado)
                    .build(idOrden))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            
            log.info("El microservicio de órdenes fue actualizado con éxito.");
        } catch (Exception e) {
            log.error("No se pudo sincronizar el estado con el microservicio de órdenes: {}", e.getMessage());
        }
    }
}