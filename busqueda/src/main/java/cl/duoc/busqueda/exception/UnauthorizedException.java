package cl.duoc.busqueda.exception;

/**
 * Excepción personalizada para indicar que el acceso a un recurso no está
 * autorizado.
 * Se utiliza para manejar errores relacionados con autenticación o falta de
 * credenciales válidas.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
