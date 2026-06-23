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

import cl.duoc.carrito.dto.CarritoRequest;
import cl.duoc.carrito.dto.CarritoResponse;
import cl.duoc.carrito.dto.UsuarioResponse;
import cl.duoc.carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/carrito")
@Slf4j
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/usuario/{idUsuario}")
     @Operation(summary= "Obtener carrito de un usuario", description= "Busca a un carrito mediante el id de un usuario")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description= "El carrito del usuario ha sido encontrado",
            content= @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioResponse.class),
            examples= @ExampleObject(
            name = "El carrito de usuario ha sido encontrado",
            value = """
                    
                    """
            ))),
        @ApiResponse(responseCode= "404", description= "El carrito del usuario no fue encontrado")
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
                schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode= "404", description= "El carrito no se pudo crear")
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
        @ApiResponse( responseCode = "200", description= "La cantidad fue actualizado correctamente",
            content= @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode= "404", description= "El usuario no se pudo actualizar")
    })
    public ResponseEntity<CarritoResponse> actualizarCarrito(@PathVariable Long id,
            @Valid @RequestBody CarritoRequest request) {
        log.info("PUT /v1/carrito/{} - Actualizando cantidades", id);

        CarritoResponse response = carritoService.actualizarCantidad(id, request.getCantidad());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary= "Elimina un producto del carrito", description= "Elimina un producto del carrito del usuario utilizando el id del producto")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "204", description= "El producto fue eliminado",
            content= @Content(mediaType = "application/json")),
        @ApiResponse(responseCode= "404", description= "El producto no se pudo eliminar")
    })
    public ResponseEntity<Void> eliminarProductoPorId(@PathVariable Long id) {
        log.info("DELETE /v1/carrito/{} - Eliminando ítem", id);

        carritoService.eliminarProductoPorId(id);
        return ResponseEntity.noContent().build();
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
