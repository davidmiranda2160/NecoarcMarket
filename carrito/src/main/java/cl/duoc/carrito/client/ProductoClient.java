package cl.duoc.carrito.client;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.carrito.dto.ProductoResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProductoClient {

    private final WebClient webClient;

    public ProductoClient(WebClient.Builder webClientBuilder,
            @Value("${service.producto.baseUrl}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public ProductoResponse obtenerProducto(Long id) {
        try {
            return this.webClient.get()
                    .uri("/v1/productos/{id}", id)
                    .retrieve()
                    .bodyToMono(ProductoResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error al conectar con el microservicio de productos para el ID: {}", id);

            // Evaluamos el código de estado HTTP devuelto por el catálogo de productos
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new NoSuchElementException("El producto con id " + id + " no existe en el catálogo.");
            } else if (ex.getStatusCode().is4xxClientError()) {
                throw new IllegalArgumentException("La petición enviada al servicio de productos es inválida.");
            } else {
                throw new RuntimeException("Error interno en el servicio de productos remoto al buscar ID: " + id);
            }
        }
    }
}
