package cl.duoc.ordenes.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.ordenes.dto.CarritoResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CarritoClient {
    @Autowired
    private WebClient webClient;

    @Value("${services.carrito.baseUrl}")
    private String carritoUrl;

    public CarritoResponse buscarUsuarioPorId(Long idUsuario) {
        try {
            return webClient.get()
                    .uri(carritoUrl +"/usuario/{idUsuario}", idUsuario)
                    .retrieve()
                    .bodyToMono(CarritoResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al comunicarse con el servicio de carrito");
            return null;
        }
    }

    public void vaciarCarrito(Long idUsuario){
        webClient.delete()
        .uri(carritoUrl + "/usuario/{usuarioId}", idUsuario)
        .retrieve()
        .toBodilessEntity()
        .block();
    }
}
