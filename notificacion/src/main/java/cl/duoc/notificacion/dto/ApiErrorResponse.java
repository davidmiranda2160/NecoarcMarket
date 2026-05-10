package cl.duoc.notificacion.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Clase que representa la respuesta de error de la API. Contiene información
 * sobre el error, como el timestamp, el status, el error, el mensaje, el path y
 * una lista de errores adicionales. Esta clase se utiliza para devolver una
 * respuesta estructurada cuando ocurre una excepción en la API.
 */
@Data
@Builder
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private List<String> errors;
}
