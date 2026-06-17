package cl.duoc.ordenes.client;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class InventarioClient {

    private final WebClient webClient;

    public InventarioClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082/v1/inventario").build();
    }

    public void descontarStock(Long productoId, Integer cantidad) {
        log.info("Solicitando descuento de stock. Producto: {}, Cantidad: {}",
                productoId, cantidad);
        webClient.put()
                .uri(uriBuilder -> uriBuilder
                .path("/producto/{productoId}/descontar")
                .queryParam("cantidad", cantidad)
                .build(productoId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public boolean tieneStockSuficiente(Long productoId, Integer cantidad) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .path("/producto/{productoId}/stock")
                .queryParam("cantidad", cantidad)
                .build(productoId))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    public void reintegrarStock(Long productoId, Integer cantidad) {
        log.info("Gatillando devolución de stock mediante /abastecer. Producto ID: {}, Cantidad: {}", productoId, cantidad);
        try {
            this.webClient.post()
                    .uri("/abastecer")
                    .bodyValue(Map.of(
                            "productoId", productoId,
                            "cantidad", cantidad
                    ))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Stock reintegrado exitosamente en el microservicio de Inventario.");
        } catch (Exception e) {
            log.error("Fallo crítico al intentar devolver stock al inventario: {}", e.getMessage());
            throw new RuntimeException("Error al sincronizar compensación de inventario: " + e.getMessage());
        }
    }

}
