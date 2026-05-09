package cl.duoc.inventario.exception;

/**
 * Excepción personalizada para conflictos (HTTP 409).
 * Se utiliza para indicar que la solicitud no se pudo completar debido a un
 * conflicto con el estado actual del recurso.
 * Por ejemplo, cuando se intenta crear un recurso que ya existe o cuando hay
 * una violación de integridad de datos.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
