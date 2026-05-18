package cl.duoc.pagos.exception;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import cl.duoc.pagos.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NoSuchElementException ex, WebRequest request) {
        log.warn("Recurso no encontrado en el servicio de pagos: {} - Detalle: {}", request.getDescription(false), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(IllegalStateException ex, WebRequest request) {
        log.warn("Conflicto de estado en Pagos: {} - Detalle: {}", request.getDescription(false), ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request); // 409 Conflict es ideal para duplicados
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Error de validacion en los datos de entrada de pagos {}: {}", request.getDescription(false), message);
        return buildResponse(HttpStatus.BAD_REQUEST, "Error de validación: " + message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Error critico no controlado en el servicio de pagos en el endpoint: {}", request.getDescription(false), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno en el servicio de pagos", request);
    }

    // Método centralizado que usa el constructor @AllArgsConstructor de tu DTO
    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, WebRequest request) {
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, status);
    }
}
