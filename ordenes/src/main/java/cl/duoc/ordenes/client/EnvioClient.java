package cl.duoc.ordenes.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.ordenes.dto.EnvioRequest;
import cl.duoc.ordenes.dto.EnvioResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EnvioClient {
    @Autowired
    private WebClient webClient;

    @Value("${services.envio.baseUrl}")
    private String envioUrl;

    public EnvioResponse crearEnvio(EnvioRequest envioRequest){
        try {
            return webClient.post()
                    .uri(envioUrl)
                    .bodyValue(envioRequest)
                    .retrieve()
                    .bodyToMono(EnvioResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al comunicarse con el servicio de envio");
            return null;
        }
    }

    public EnvioResponse obtenerEnvioPorId(Long idEnvio){
        try {
            return webClient.get()
                .uri(envioUrl + "v1/envios/"+ idEnvio)
                .retrieve()
                .bodyToMono(EnvioResponse.class)
                .block();
        } catch (Exception e) {
            log.error("Error al comunicarse al servicio de envios: {}", e.getMessage());
            return null;
        }
    }
}
