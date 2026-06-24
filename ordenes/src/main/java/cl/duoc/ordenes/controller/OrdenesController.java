package cl.duoc.ordenes.controller;

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

import cl.duoc.ordenes.dto.ApiErrorResponse;
import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.service.OrdenesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/ordenes")
@Tag(name = "Ordenes", description = "Gestion de las compras y sincronizacion de estados")
@RequiredArgsConstructor
public class OrdenesController {

    private final OrdenesService ordenesService;

    
    @PostMapping
    @Operation(summary= "Crear una orden para un cliente", description= "Se crea una orden para un usuario ingresando el id del carrito que tiene asociado, borrando su carrito y guardando lo que compro en el detalle")
    @ApiResponses(value = {
        @ApiResponse(responseCode= "201", description= "Crear una orden",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = OrdenesResponse.class),
        examples= @ExampleObject(
            name = "La orden del cliente fue creada correctamente",
            value = (
                """
                    {
                        "id": 5,
                        "usuarioId": 5,
                        "total": 207.92,
                        "estadoOrden": "Pendiente",
                        "fechaOrden": "2026-06-23T21:40:31.1164229",
                        "items": null,
                        "detalles": [
                        {
                        "idProducto": 1,
                        "cantidad": 8,
                        "precioUnitario": 25.99
                    }
                ]
            }
                """
            )))),
        @ApiResponse(responseCode= "404", description= "No se pudo crear la orden",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples= @ExampleObject(
            name = "La orden del cliente fue creada correctamente",
            value = (
                """
                    {
                        "error": "Not Found",
                        "message": "No hay productos en el carrito para generar una orden.",
                        "path": "/v1/ordenes",
                        "status": 404,
                        "timestamp": "2026-06-23T22:48:43.0635401"
                    }
                """
        )))),
        })
    public ResponseEntity<OrdenesResponse> crearOrden(@Valid @RequestBody OrdenesRequest request) {
        return new ResponseEntity<>(ordenesService.crearOrden(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary= "Lista a todos las ordenes", description= "Busca y trae a todas los ordenes de los clientes del sistema")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description= "Las ordenes fueron listados",
            content= @Content(mediaType = "application/json",
                array= @ArraySchema(schema = @Schema(implementation = OrdenesResponse.class)))),
    })
    public ResponseEntity<List<OrdenesResponse>> listarTodas() {
        return ResponseEntity.ok(ordenesService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary= "Obtener una orden", description= "Busca a una orden mediante su id")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description= "La orden fue encontrada",
            content= @Content(mediaType = "application/json",
            schema = @Schema(implementation = OrdenesResponse.class),
            examples = @ExampleObject(
            name = "El usuario ha sido encontrado",
            value = """
                        {
                        "id": 5,
                        "usuarioId": 5,
                        "total": 207.92,
                        "estadoOrden": "Pendiente",
                        "fechaOrden": "2026-06-23T21:40:31.1164229",
                        "items": null,
                        "detalles": [
                        {
                        "idProducto": 1,
                        "cantidad": 8,
                        "precioUnitario": 25.99
                    }
                ]
            }
                   """
                ))),
        @ApiResponse(responseCode= "404", description= "La orden no existe",
            content=  @Content(mediaType = "application/json",
            schema= @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
            name = "Usuario no encontrado",
            value = """
                    {
                        "error": "Not Found",
                        "message": "Orden no encontrada con ID: 6",
                        "path": "/v1/ordenes/6",
                        "status": 404,
                        "timestamp": "2026-06-23T23:00:38.3663343"
                    }
                    """
                    )))
    })
    public ResponseEntity<OrdenesResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenesService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener una orden", description = "Busca a una orden mediante su id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "La orden fue encontrada",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = OrdenesResponse.class)),
            examples = @ExampleObject(
            name = "La orden ha sido encontrada",
            value = """
                    {
                        "id": 5,
                        "usuarioId": 5,
                        "total": 207.92,
                        "estadoOrden": "Pendiente",
                        "fechaOrden": "2026-06-23T21:40:31.1164229",
                        "items": null,
                        "detalles": [
                            {
                                "idProducto": 1,
                                "cantidad": 8,
                                "precioUnitario": 25.99
                            }
                        ]
                    }
                    """
                ))),
        @ApiResponse(responseCode = "404", description = "La orden no existe",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
            name = "Orden no encontrada",
            value = """
                    {
                        "error": "Not Found",
                        "message": "Orden no encontrada con ID: 6",
                        "path": "/v1/ordenes/6",
                        "status": 404,
                        "timestamp": "2026-06-23T23:00:38.3663343"
                    }
                    """
                    )))
    })
    public ResponseEntity<List<OrdenesResponse>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ordenesService.obtenerPorUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener ordenes mediante su estado", description = "Buscar una orden mediante el estado en el que se encuentre en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "La orden fue encontrada",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = OrdenesResponse.class)), // Ajustado a Array
            examples = @ExampleObject(
            name = "Lista de ordenes por estado",
            value = """
                    [
                        {
                            "id": 5,
                            "usuarioId": 5,
                            "total": 207.92,
                            "estadoOrden": "Pendiente",
                            "fechaOrden": "2026-06-23T21:40:31.1164229",
                            "items": null,
                            "detalles": [
                                {
                                    "idProducto": 1,
                                    "cantidad": 8,
                                    "precioUnitario": 25.99
                                }
                            ]
                        }
                    ]
                    """
                ))),
        @ApiResponse(responseCode = "404", description = "La orden con estado n no existe",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
            name = "Estado no encontrado",
            value = """
                    {
                        "error": "Not Found",
                        "message": "No se encontraron ordenes con el estado: n",
                        "path": "/v1/ordenes/estado/n",
                        "status": 404,
                        "timestamp": "2026-06-23T23:14:25.0567576"
                    }
                    """
                    )))
    })
    public ResponseEntity<List<OrdenesResponse>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ordenesService.obtenerPorEstado(estado));
    }

    @PutMapping("/{idOrden}/estado")
    @Operation(
        summary = "Proceso interno para actualizar estado desde el microservicio de Pagos", 
        description = "Endpoint exclusivo para comunicación mediante WebClient que permite al microservicio de pagos notificar si la orden fue aprobada o rechazada para gestionar el stock."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Estado de la orden actualizado correctamente y stock sincronizado si corresponde"),
        @ApiResponse(responseCode = "404", description = "No existe la orden con el id proporcionado")
    })
    public ResponseEntity<Void> actualizarEstadoDesdePagos(
            @PathVariable Long idOrden,
            @RequestParam String estado) {

        ordenesService.actualizarEstadoDesdePagos(idOrden, estado);
        return ResponseEntity.noContent().build();
    }
}
