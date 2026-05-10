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
@RequestMapping("/carrito")
@Slf4j
public class CarritoController {
    @Autowired CarritoService carritoService;

    @GetMapping("/{id}")
    public List<CarritoResponse> obtenerCarritoPorUsuario(@PathVariable Long id) {
    return carritoService.obtenerCarritoPorUsuario(id);
    }

    @PostMapping
    public ResponseEntity<CarritoResponse> agregarProducto(@Valid @RequestBody CarritoRequest request) {
        log.info("");
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.agregarProducto(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarritoResponse> actualizarPaciente(@PathVariable Long id,
        @Valid @RequestBody CarritoRequest request) {
        log.info("", id);
        return ResponseEntity
        .ok()
        .body(carritoService.actualizarCantidad(id, request.getCantidad(), request.getMontoTotal()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoPorId(@PathVariable Long id) {
        log.info("", id);
        carritoService.eliminarProductoPorId(id);
        return ResponseEntity.noContent().build();
    }

}
