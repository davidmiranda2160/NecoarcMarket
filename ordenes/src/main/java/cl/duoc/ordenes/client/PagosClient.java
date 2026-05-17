package cl.duoc.ordenes.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.ordenes.dto.PagosRequest;
import cl.duoc.ordenes.dto.PagosResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PagosClient {

    private final WebClient webClient;

    public PagosClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8092/v1/pagos").build();
    }

    public void procesarPago(PagosRequest request) {
        try {
            log.info("Enviando orden {} a pagos", request.getIdOrden());
            this.webClient.post()
                    .uri("/crear")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PagosResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error crítico al conectar con Pagos para la orden {}: {}",
                    request.getIdOrden(), e.getMessage());
            throw new RuntimeException("No se pudo procesar el pago, intente más tarde.");
        }
    }
}
