package cl.duoc.envio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.envio.dto.ApiErrorResponse;
import cl.duoc.envio.dto.EnvioRequest;
import cl.duoc.envio.dto.EnvioResponse;
import cl.duoc.envio.service.EnvioService;
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
@Slf4j
@RequestMapping("v1/envio")
@Tag(name = "Envios", description = "Gestión de despachos y seguimiento de ordenes")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @PostMapping
    @Operation(summary = "Crear nuevo envio", description = "Registra un envío asociado a una orden existente y genera automaticamente un codigo de seguimiento.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Envio creado exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = EnvioResponse.class),
            examples = @ExampleObject(
                name = "Envío creado correctamente",
                value = """
                    {
                        "id": 5,
                        "codigoSeguimiento": "TRACK-NECO-005",
                        "estadoEnvio": "Preparando",
                        "empresaTransporte": "Chile Express",
                        "fechaEstimadaEntrega": "2026-06-27",
                        "direccionDestino": "Calle falsa 123",
                        "fechaCreacion": "2026-06-24T06:22:12",
                        "orden": {
                            "id": 5,
                            "usuarioId": 5,
                            "total": 129.95,
                            "estadoOrden": "Pagada",
                            "fechaOrden": "2026-06-24T06:21:35",
                            "detalles": [
                                {
                                    "idProducto": 1,
                                    "cantidad": 5,
                                    "precioUnitario": 25.99
                                }
                            ]
                        }
                    }
                    """
            ))),
        @ApiResponse(responseCode = "409", description = "La orden asociada no existe", 
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
                name = "Error orden no encontrada",
                value = """
                    {
                    "timestamp": "2026-06-24T06:42:13.0404523",
                    "status": 409,
                    "error": "Conflict",
                    "message": "Ya existe un pago registrado para la orden: 5",
                    "path": "/v1/pagos"
                    }
                    """
            ))),
         @ApiResponse(responseCode = "404", description = "La orden asociada no existe", 
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
                name = "Error orden no encontrada",
                value = """
                    {
                    "timestamp": "2026-06-24T06:36:40.0168966",
                    "status": 404,
                    "error": "Not Found",
                    "message": "No se encontró la orden con id: 6",
                    "path": "/v1/envio",
                    "errors": null
                    }
                    """
            )))
    })
    public ResponseEntity<EnvioResponse> crearEnvio(@Valid @RequestBody EnvioRequest request) {
        EnvioResponse response = envioService.crearEnvio(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos los envíos", description = "Recupera el historial completo de envíos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    public ResponseEntity<List<EnvioResponse>> listarTodos() {
        return ResponseEntity.ok(envioService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener envío por ID", description = "Busca los detalles de un envío específico mediante su ID interno.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Envio encontrado",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = EnvioResponse.class),
            examples = @ExampleObject(
                name = "Envío creado correctamente",
                value = """
                    {
                        "id": 5,
                        "codigoSeguimiento": "TRACK-NECO-005",
                        "estadoEnvio": "Preparando",
                        "empresaTransporte": "Chile Express",
                        "fechaEstimadaEntrega": "2026-06-27",
                        "direccionDestino": "Calle falsa 123",
                        "fechaCreacion": "2026-06-24T06:22:12",
                        "orden": {
                            "id": 5,
                            "usuarioId": 5,
                            "total": 129.95,
                            "estadoOrden": "Pagada",
                            "fechaOrden": "2026-06-24T06:21:35",
                            "detalles": [
                                {
                                    "idProducto": 1,
                                    "cantidad": 5,
                                    "precioUnitario": 25.99
                                }
                            ]
                        }
                    }
                    """
            ))),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado", 
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
                name = "Envío creado correctamente",
                value = """
                {
                    "timestamp": "2026-06-24T06:47:46.5006742",
                    "status": 404,
                    "error": "Not Found",
                    "message": "No se encontró el envío solicitado con id: 6",
                    "path": "/v1/envio/6",
                    "errors": null
                }
                    """
            )))
    })
    public ResponseEntity<EnvioResponse> obtenerPorId(@PathVariable Long id) {
        EnvioResponse response = envioService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de envío", description = "Cambia el estado actual de un envío (Ej: EN_CAMINO, ENTREGADO).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = EnvioResponse.class),
            examples = @ExampleObject(
                name = "Estado actualizado exitosamente",
                value = """
                    {
                        "id": 5,
                        "codigoSeguimiento": "TRACK-NECO-005",
                        "estadoEnvio": "ENTREGADO",
                        "empresaTransporte": "Chile Express",
                        "fechaEstimadaEntrega": "2026-06-27",
                        "direccionDestino": "Calle falsa 123",
                        "fechaCreacion": "2026-06-24T06:22:12",
                        "orden": {    
                        "id": 5,
                        "usuarioId": 5,
                        "total": 129.95,
                        "estadoOrden": "Pagada",
                        "fechaOrden": "2026-06-24T06:21:35",
                        "detalles": [
                        {
                        "idProducto": 1,
                        "cantidad": 5,
                        "precioUnitario": 25.99
                    }
                ]
            }
        }
                    """
            ))),
        @ApiResponse(responseCode = "404", description = "Estado inválido o vacío", 
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
                name = "Estado inválido o vacío",
                value = """
                    {
                        "timestamp": "2026-06-24T07:01:09.4169806",
                        "status": 404,
                        "error": "Not Found",
                        "message": "No se puede actualizar el estado porque no existe el envio con id: 0",
                        "path": "/v1/envio/0/estado",
                        "errors": null
                    }
                    """
            )))
    })
    public ResponseEntity<EnvioResponse> actualizarEstado(
            @PathVariable Long id, 
            @RequestParam String nuevoEstado) {
        EnvioResponse response = envioService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(response);
    }
}