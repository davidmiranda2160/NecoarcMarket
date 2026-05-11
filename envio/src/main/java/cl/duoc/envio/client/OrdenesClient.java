package cl.duoc.envio.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.envio.dto.OrdenesResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrdenesClient {

    private WebClient webClient;

    public OrdenesResponse buscarOrdenPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/ordenes/{id}", id)
                    .retrieve()
                    .bodyToMono(OrdenesResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al comunicarse con el servicio de ordenes");
            return null;
        }
    }
}
