package cl.duoc.usuario.Client;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.usuario.dto.PagosRequest;
import cl.duoc.usuario.dto.PagosResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PagosClient {

    private final WebClient webClient;

    public PagosClient(WebClient.Builder webClientBuilder,
            @Value("${services.pagos.baseUrl}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    
    public PagosResponse crearPago(PagosRequest request) {
        try {
            return this.webClient.post()
                    .uri("/v1/pagos/crear")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PagosResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error al intentar procesar un pago");
            manejarError(ex);
            return null;
        }
    }

    public List<PagosResponse> listarPagos() {
        try {
            return this.webClient.get()
                    .uri("/pagos")
                    .retrieve()
                    .bodyToFlux(PagosResponse.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error al obtener el listado de pagos");
            manejarError(ex);
            return null;
        }
    }

    private void manejarError(WebClientResponseException ex) {
        if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            throw new NoSuchElementException("Recurso de pagos no encontrado.");
        } else if (ex.getStatusCode().is4xxClientError()) {
            throw new IllegalArgumentException("Error en los datos enviados al servicio de pagos.");
        } else {
            throw new RuntimeException("Error en el servidor de pagos remoto.");
        }
    }
}
