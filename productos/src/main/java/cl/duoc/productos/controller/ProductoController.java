package cl.duoc.productos.controller;

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

import cl.duoc.productos.dto.ProductoRequest;
import cl.duoc.productos.dto.ProductoResponse;
import cl.duoc.productos.dto.ProductoUpdateRequest;
import cl.duoc.productos.model.Producto;
import cl.duoc.productos.service.ProductoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/productos")
@Slf4j
@Tag(name = "Producto", description = "Operaciones relacionadas con el microservicio de productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarProductos() {
        log.info("GET /v1/productos - Solicitando catálogo completo con stock");
        return ResponseEntity.ok(productoService.buscarProductos());
    }

    @GetMapping("/{id}")
    public ProductoResponse buscarProductoPorId(@PathVariable Long id){
        log.info("GET /v1/productos/{}", id);
        return productoService.buscarProductoPorId(id);
    }

    //obtener cosa por nombre
    @GetMapping("/nombrep/{nombrep}")
    public ProductoResponse buscarProductoPorNombre(@PathVariable String nombrep){
        log.info("GET /v1/productos/buscarProductoPorNombre/{}", nombrep);
        return productoService.buscarProductoPorNombre(nombrep);
    }

    @PostMapping()
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody ProductoRequest request){
        log.info("POST /v1/productos");
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crearProducto(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoUpdateRequest request){
        log.info("PUT /v1/productos/{}", id);
        return ResponseEntity.ok().body(productoService.actualizarProducto(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id){
        log.info("DELETE /api/productos/eliminarProducto/{}", id);
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
