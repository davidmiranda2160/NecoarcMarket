package cl.duoc.pagos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.pagos.dto.ApiErrorResponse;
import cl.duoc.pagos.dto.PagosRequest;
import cl.duoc.pagos.dto.PagosResponse;
import cl.duoc.pagos.service.PagosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("v1/pagos")
@Slf4j
@Tag(name = "Pagos", description = "Gestión de transacciones y estados de pago")
@RequiredArgsConstructor
public class PagosController {

    private final PagosService pagosService;

    @GetMapping
    @Operation(summary = "Listar todos los pagos", description = "Recupera un listado completo con el historial de transacciones registradas en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de pagos recuperado con exito")
    })
    public ResponseEntity<List<PagosResponse>> listarTodos() {
        log.info("Petición recibida para listar todos los pagos");
        return ResponseEntity.ok(pagosService.listarTodosLosPagos());
    }

    @GetMapping("/orden/{id}")
    @Operation(summary = "Obtener pago por id de orden", description = "Busca el registro de pago asociado a una orden de compra especifica.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago localizado correctamente",
            content = @Content(mediaType = "application/json", 
            schema= @Schema(implementation= ApiErrorResponse.class),
            examples= @ExampleObject(
            """
            {
                "id": 5,
                "idOrden": 5,
                "metodoPago": "Web-pay",
                "montoAPagar": 129.95,
                "montoPagado": 129.95,
                "fechaTransaccion": "2026-06-24T04:26:28",
                "estadoPago": "APROBADO"
            }
            """
            ))),
        @ApiResponse(responseCode = "404", description = "No se encontró ningún pago para la orden indicada",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
                name = "Pago no encontrado",
                value = """
                    {
                        "error": "Not Found",
                        "message": "No se encontró un pago para la orden: 6",
                        "path": "/v1/pagos/orden/45",
                        "status": 404,
                        "timestamp": "2026-06-24T03:00:00.000"
                    }
                """
            )))
    })
    public ResponseEntity<PagosResponse> obtenerPagoPorIdOrden(@PathVariable("id") Long id) {
        log.info("Buscando pago asociado a la orden ID: {}", id);
        return ResponseEntity.ok(pagosService.obtenerPagoPorOrden(id));
    }

    @PostMapping
    @Operation(
        summary = "Procesar el pago de una orden", 
        description = "Recibe la solicitud de pago, valida que no esté duplicado y simula un proceso bancario. " +
                      "si el metodo es 'rechazado' se cancelara la orden por el contrario si marca como 'Pagada' interactua con el microservicio de ordenes."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pago procesado y registrado con exito",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = PagosResponse.class),
            examples = @ExampleObject(
                name = "Transacción aprobada con éxito",
                value = """
                    {
                        "id": 5,
                        "idOrden": 5,
                        "metodoPago": "Web-pay",
                        "montoAPagar": 129.95,
                        "montoPagado": 129.95,
                        "fechaTransaccion": "2026-06-24T04:26:27.8745203",
                        "estadoPago": "APROBADO"
                    }
                """
            ))),
        @ApiResponse(responseCode = "409", description = "Intento de pago duplicado para la misma orden",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
                name = "Pago duplicado",
                value = """
                    {
                    "timestamp": "2026-06-24T04:28:11.108854",
                    "status": 409,
                    "error": "Conflict",
                    "message": "Ya existe un pago registrado para la orden: 5",
                    "path": "/v1/pagos"
                    }
                """
            )))
    })
    public ResponseEntity<PagosResponse> procesarPago(@Valid @RequestBody PagosRequest request) {
        log.info("Procesando nuevo pago para la orden: {}", request.getIdOrden());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagosService.procesarPago(request));
    }

}
