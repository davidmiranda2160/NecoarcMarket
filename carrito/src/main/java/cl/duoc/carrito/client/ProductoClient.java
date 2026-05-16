package cl.duoc.carrito.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.carrito.dto.ProductoResponse;

@Component
public class ProductoClient {
    private final WebClient webClient;
    
    public ProductoClient(WebClient.Builder webClientBuilder, 
        @Value("${service.producto.baseUrl}") String baseUrl){
            this.webClient = webClientBuilder.baseUrl(baseUrl)
            .build();
        }

        public ProductoResponse obtenerProducto(Long id){
            return this.webClient.get()
                    .uri("/v1/productos/{id}", id)
                    .retrieve()
                    .bodyToMono(ProductoResponse.class)
                    .block();
        }
}
