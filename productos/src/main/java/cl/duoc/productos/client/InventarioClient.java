package cl.duoc.productos.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.productos.dto.InventarioRequest;
import cl.duoc.productos.dto.InventarioResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class InventarioClient {
    @Autowired
    private WebClient webClient; 

    public InventarioResponse crearRegistroInventario(InventarioRequest request) {
        log.info("Llamando a Inventario para crear registro del producto: {}", request.getProductoId());
        try {
            return webClient.post()
                    .uri("/abastecer") 
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(InventarioResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error al crear inventario. Código: {}", ex.getStatusCode());
            throw new RuntimeException("No se pudo inicializar el inventario para el producto.");
        }
    }
    public InventarioResponse obtenerStockPorProducto(Long productoId) {
    try {
        return webClient.get()
                .uri("/producto/{id}", productoId)
                .retrieve()
                .bodyToMono(InventarioResponse.class)
                .block();
    } catch (WebClientResponseException ex) {
        if (ex.getStatusCode().value() == 404) {
            return InventarioResponse.builder().productoId(productoId).cantidad(0).build();
        }
        throw new RuntimeException("Error al consultar inventario");
    }
    }

}
