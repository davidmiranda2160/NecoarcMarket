package cl.duoc.carrito.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import cl.duoc.carrito.service.CarritoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/carrito")
@Slf4j
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<CarritoResponse>> obtenerCarritoPorUsuario(@PathVariable Long idUsuario) {
        log.info("GET /v1/carrito/usuario/{}", idUsuario);
        List<CarritoResponse> carrito = carritoService.obtenerCarritoPorUsuario(idUsuario);
        return ResponseEntity.ok(carrito); // Retorna 200 OK con la lista de productos
    }

    @PostMapping("/{idUsuario}/{idProducto}")
    public ResponseEntity<CarritoResponse> agregarProducto(@PathVariable Long idUsuario,
            @PathVariable Long idProducto,
            @Valid @RequestBody CarritoRequest request) {
        log.info("POST /v1/carrito/{}/{} - Intentando agregar producto", idUsuario, idProducto);

        CarritoResponse response = carritoService.agregarProducto(request, idUsuario, idProducto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarritoResponse> actualizarCarrito(@PathVariable Long id,
            @Valid @RequestBody CarritoRequest request) {
        log.info("PUT /v1/carrito/{} - Actualizando cantidades", id);

        CarritoResponse response = carritoService.actualizarCantidad(id, request.getCantidad(), request.getMontoTotal());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoPorId(@PathVariable Long id) {
        log.info("DELETE /v1/carrito/{} - Eliminando ítem", id);

        carritoService.eliminarProductoPorId(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<Void> limpiarCarrito(@PathVariable Long idUsuario) {
        log.info("Vaciando carrito del usuario: {}", idUsuario);
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
