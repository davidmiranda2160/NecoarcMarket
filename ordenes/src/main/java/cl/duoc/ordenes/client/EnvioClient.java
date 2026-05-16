package cl.duoc.ordenes.client;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.ordenes.dto.EnvioRequest;
import cl.duoc.ordenes.dto.EnvioResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EnvioClient {
   private final WebClient webClient;

    public EnvioClient(WebClient.Builder builder, @Value("${services.envio.baseUrl}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public EnvioResponse crearEnvio(EnvioRequest envioRequest) {
        try {
            return webClient.post()
                    .uri("/v1/envios")
                    .bodyValue(envioRequest)
                    .retrieve()
                    .bodyToMono(EnvioResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error en servicio de envíos: {}", ex.getResponseBodyAsString());
            throw new IllegalArgumentException("No se pudo generar el envío: " + ex.getMessage());
        }
    }

    public EnvioResponse obtenerEnvioPorId(Long idEnvio) {
        try {
            return webClient.get()
                    .uri("/v1/envios/{id}", idEnvio)
                    .retrieve()
                    .bodyToMono(EnvioResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                // Si el ID de envío no existe en el micro de envíos, lanzamos 404
                throw new NoSuchElementException("No se encontró información de envío con ID: " + idEnvio);
            }
            log.error("Error al consultar el servicio de envíos: {}", ex.getMessage());
            throw new RuntimeException("Error de comunicación con el servicio de envíos");
        }
    }
}
