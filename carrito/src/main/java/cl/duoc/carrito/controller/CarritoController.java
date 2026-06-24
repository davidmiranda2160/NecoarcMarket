package cl.duoc.carrito.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.carrito.dto.ApiErrorResponse;
import cl.duoc.carrito.dto.CarritoRequest;
import cl.duoc.carrito.dto.CarritoResponse;
import cl.duoc.carrito.service.CarritoService;
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
@RequestMapping("/v1/carrito")
@Slf4j
@Tag(name = "Carrito", description = "Gestión de productos seleccionados en el carrito de compras")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary= "Obtener carrito de un usuario", description= "Busca a un carrito mediante el id de un usuario")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description= "El carrito del usuario ha sido encontrado",
            content= @Content(mediaType = "application/json",
            schema = @Schema(implementation = CarritoResponse.class),
            examples= @ExampleObject(
            name = "El carrito de usuario ha sido encontrado",
            value = """
                        {
                            "id": 1,
                            "cantidad": 1,
                            "montoTotal": 25.99,
                            "usuario": {
                            "id": 1,
                            "nombre": "Carlos",
                            "appaterno": "Soto",
                            "apmaterno": "Espinoza",
                            "correo": "c.soto@gmail.com",
                            "direccion": "Alameda 1020",
                            "telefono": "+56944443333",
                            "tipoUsuario": "Cliente"
                            },
                            "producto": {
                            "id": 1,
                            "nombrep": "Peluche de Neco-Arc",
                            "descripcion": "Peluche oficial de 20cm, edición limitada Burunyuu.",
                            "precio": 25.99
                        }
                    }
                    """
            ))),
        @ApiResponse(responseCode= "404", description= "El carrito del usuario no fue encontrado",
            content=  @Content(mediaType = "application/json",
            schema= @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
            name = "Carrito del usuario no fue encontrado",
            value = """        
                        {
                           "timestamp": "2026-06-23T18:21:02.1521734",
                            "status": 404,
                            "error": "Not Found",
                            "message": "No se encontró el usuario con id: 0",
                            "path": "/v1/carrito/usuario/0",
                            "errors": null
                        }
                """
                    ))),
    })
    public ResponseEntity<List<CarritoResponse>> obtenerCarritoPorUsuario(@PathVariable Long idUsuario) {
        log.info("GET /v1/carrito/usuario/{}", idUsuario);
        List<CarritoResponse> carrito = carritoService.obtenerCarritoPorUsuario(idUsuario);
        return ResponseEntity.ok(carrito); // Retorna 200 OK con la lista de productos
    }

    @PostMapping("/{idUsuario}/{idProducto}")
    @Operation(summary= "Crear un carrito", description= "Crea un crea un carrito usando el id del usuario y el del producto que desea")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "201", description= "El carrito fue creado",
            content= @Content(mediaType = "application/json",
                schema = @Schema(implementation = CarritoResponse.class),
            examples= @ExampleObject(
                name = "El carrito fue creado",
                value= """   
                        {
                            "id": 5,
                            "cantidad": 5,
                            "montoTotal": 129.95,
                            "usuario": {
                            "id": 5,
                            "nombre": "Estaban",
                            "appaterno": "Quinto",
                            "apmaterno": "Gonzales",
                            "correo": "e.quinto@gmail.com",
                            "direccion": "Calle falsa 123",
                            "telefono": "+569778996777",
                            "tipoUsuario": "Cliente"
                            },
                            "producto": {
                            "id": 1,
                            "nombrep": "Peluche de Neco-Arc",
                            "descripcion": "Peluche oficial de 20cm, edición limitada Burunyuu.",
                            "precio": 25.99
                            }
                        }
                       """
            ))),
        @ApiResponse(responseCode= "400", description= "El carrito no se pudo crear",
            content= @Content(mediaType = "application/json",
            schema= @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
            name = "El carrito no se pudo crear",
            value = """
                    {
                        "timestamp": "2026-06-23T18:53:20.6823579",
                        "status": 400,
                        "error": "Bad Request",
                        "message": "Error de validación en los datos enviados",
                        "path": "/v1/carrito/5/1",
                        "errors": [
                        "cantidad: La cantidad mínima es 1"]
                    }
                    """
                )))
    })
    public ResponseEntity<CarritoResponse> agregarProducto(@PathVariable Long idUsuario,
            @PathVariable Long idProducto,
            @Valid @RequestBody CarritoRequest request) {
        log.info("POST /v1/carrito/{}/{} - Intentando agregar producto", idUsuario, idProducto);

        CarritoResponse response = carritoService.agregarProducto(request, idUsuario, idProducto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary= "Actualizar a la cantidad de productos", description= "Actualiza la cantidad de productos de un carrito")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description= "La cantidad fue actualizada correctamente",
            content= @Content(mediaType = "application/json",
            schema = @Schema(implementation = CarritoResponse.class),
            examples= @ExampleObject(
                name= "La cantidad fue actualizada correctamente",
                value =(
                """
                    {        
                        "id": 5,
                        "cantidad": 3,
                        "montoTotal": 77.97,
                        "usuario": {
                        "id": 5,
                        "nombre": "Estaban",
                        "appaterno": "Quinto",
                        "apmaterno": "Gonzales",
                        "correo": "e.quinto@gmail.com",
                        "direccion": "Calle falsa 123",
                        "telefono": "+569778996777",
                        "tipoUsuario": "Cliente"
                        },
                        "producto": {
                        "id": 1,
                        "nombrep": "Peluche de Neco-Arc",
                        "descripcion": "Peluche oficial de 20cm, edición limitada Burunyuu.",
                        "precio": 25.99
                        }
                    }
    """
        )))),
        @ApiResponse(responseCode = "400", description = "No se pudo actualizar, la cantidad tiene que ser mayor a 1",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(
                name = "La cantidad minima es 1",
                value = """    
                            {
                                "timestamp": "2026-06-23T19:14:51.8573133",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Error de validación en los datos enviados",
                                "path": "/v1/carrito/0",
                                "errors": [
                                "cantidad: La cantidad mínima es 1"
                                ]
                            }    
                    """
            )))
        })
    public ResponseEntity<CarritoResponse> actualizarCarrito(@PathVariable Long id,
            @Valid @RequestBody CarritoRequest request) {
        log.info("PUT /v1/carrito/{} - Actualizando cantidades", id);

        CarritoResponse response = carritoService.actualizarCantidad(id, request.getCantidad());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/usuario/{idUsuario}")
    @Operation(summary= "Vaciar un carrito", description= "Utiliza el id del usuario y llama al metodo para vaciar el carrito de un usuario especifico")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "204", description= "El carrito fue vaciado",
            content= @Content(mediaType = "application/json")),
        @ApiResponse(responseCode= "404", description= "El carrito no se pudo vaciar")
    })
    public ResponseEntity<Void> limpiarCarrito(@PathVariable Long idUsuario) {
        log.info("Vaciando carrito del usuario: {}", idUsuario);
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.noContent().build();
    }
}