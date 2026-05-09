package cl.duoc.inventario.client;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.inventario.dto.ProductoResponse;
import cl.duoc.inventario.exception.ForbiddenException;
import cl.duoc.inventario.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProductoClient {
    @Autowired
    private WebClient webClient;

    public ProductoClient(WebClient webClient){
        this.webClient = webClient;
    }
    public ProductoResponse obtenerProductoPorId(Long id) {
        log.info("Llamando al microservicio de Productos para el ID: {}", id);
        try {
            return webClient.get()
                    //Revisar bien la asignacion del endpoint o me va a cargar la verga
                    .uri("/{id}", id) 
                    .retrieve()
                    .bodyToMono(ProductoResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error al comunicar con Productos para el ID: {}. Estado: {}", id, ex.getStatusCode());
            switch (ex.getStatusCode().value()) {
                case 404 -> throw new NoSuchElementException("El producto con ID " + id + " no existe en el catálogo.");
                case 401 -> throw new UnauthorizedException("No autorizado para consultar el catálogo de productos.");
                case 403 -> throw new ForbiddenException("Acceso prohibido al catálogo de productos.");
                default -> throw new RuntimeException("Error al consultar el microservicio de productos: " + ex.getMessage());
            }
        } catch (Exception e) {
            log.error("Error inesperado en ProductoClient: ", e);
            throw new RuntimeException("Error de conexión entre microservicios.");
        }
    }
}
