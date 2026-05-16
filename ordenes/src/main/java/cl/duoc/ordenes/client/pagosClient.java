package cl.duoc.ordenes.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.ordenes.dto.PagosRequest;
import cl.duoc.ordenes.dto.PagosResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PagosClient {
    @Autowired
    private WebClient webClient;

    @Value("${services.pagos.baseUrl}")
    private String pagosUrl;

    public PagosResponse procesarPago(PagosRequest pagosRequest){
        try {
            return webClient.post()
                    .uri(pagosUrl + "/procesar")
                    .bodyValue(pagosRequest)
                    .retrieve()
                    .bodyToMono(PagosResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al comunicarse con el servicio de pagos");
            return null;
        }
    }

    public PagosResponse obtenerPagoPorOrden(Long idOrden){
        try {
            return webClient.get()
                .uri(pagosUrl + "/orden/" + idOrden)
                .retrieve()
                .bodyToMono(PagosResponse.class)
                .block();
        } catch (Exception e) {
            log.error("Error al comunicarse con el servicio de pagos para obtener la orden {}", e.getMessage());
            return null;
        }
    }

}
