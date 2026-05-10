package cl.duoc.resena.exception;

/**
 * Excepción personalizada para indicar que el acceso a un recurso está
 * prohibido.
 * Se utiliza para manejar errores relacionados con permisos insuficientes o
 * intentos de acceso no autorizados.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
