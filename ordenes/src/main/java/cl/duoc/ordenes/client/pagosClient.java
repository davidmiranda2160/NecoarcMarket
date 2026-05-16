package cl.duoc.ordenes.client;

import java.util.NoSuchElementException; // Importación faltante

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.ordenes.dto.PagosRequest;
import cl.duoc.ordenes.dto.PagosResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PagosClient {

    private final WebClient webClient;

    // Constructor corregido: Inyecta el builder y configura la baseUrl
    public PagosClient(WebClient.Builder builder, @Value("${services.pagos.baseUrl}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public PagosResponse procesarPago(PagosRequest pagosRequest) {
        try {
            return webClient.post()
                    .uri("/v1/pagos/procesar")
                    .bodyValue(pagosRequest)
                    .retrieve()
                    .bodyToMono(PagosResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            int codigoError = ex.getStatusCode().value();
            log.error("Error en servicio de pagos. Código recibido: {}", codigoError);
            throw new IllegalArgumentException("La pasarela de pago respondió con error código: " + codigoError);
        }
    }

    public PagosResponse obtenerPagoPorOrden(Long idOrden) {
        try {
            return webClient.get()
                    .uri("/v1/pagos/orden/{idOrden}", idOrden)
                    .retrieve()
                    .bodyToMono(PagosResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            // Usamos value() para comparar el código numérico
            if (ex.getStatusCode().value() == 404) {
                throw new NoSuchElementException("No se encontró un registro de pago para la orden: " + idOrden);
            }

            log.error("Error al consultar el pago de la orden {}. Status: {}", idOrden, ex.getStatusCode());
            throw new RuntimeException("Error de comunicación con el servicio de pagos.");
        }
    }
}
