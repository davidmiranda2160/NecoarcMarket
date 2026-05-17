package cl.duoc.inventario.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.inventario.dto.InventarioRequest;
import cl.duoc.inventario.dto.InventarioResponse;
import cl.duoc.inventario.dto.ProductoDetalleDTO;
import cl.duoc.inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/inventario")
@Slf4j
public class InventarioController {
    @Autowired
    private InventarioService inventarioService;

    @PostMapping("/abastecer")
    public ResponseEntity<InventarioResponse> abastecer(@Valid @RequestBody InventarioRequest request){
        return ResponseEntity.ok(inventarioService.agregarStock(request.getProductoId(), request.getCantidad()));

    }

    //para descontar cantidad de productos en el inventario
    @PutMapping("/producto/{productoId}/descontar")
    public ResponseEntity<InventarioResponse> descontarStock(
            @PathVariable Long productoId, 
            @RequestParam Integer cantidad) {
        log.info("PUT /v1/inventario/producto/{}/descontar?cantidad={}", productoId, cantidad);
        InventarioResponse response = inventarioService.descontarStock(productoId, cantidad);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/producto/{id}")
    public ResponseEntity<InventarioResponse> obtenerStockPlano(@PathVariable Long id){
        log.info("GET /v1/inventario/producto/{} - Solicitud de stock plano para Productos", id);
        InventarioResponse response = inventarioService.obtenerStockPorProducto(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/producto/{id}/detalle")
    public ResponseEntity<ProductoDetalleDTO> obtenerDetalleCompleto(@PathVariable Long id){
        log.info("GET /v1/inventario/producto/{}/detalle - Solicitud de información cruzada", id);
        ProductoDetalleDTO detalle = inventarioService.obtenerDetalleCompleto(id);
        return ResponseEntity.ok(detalle);
    }

    @DeleteMapping("/producto/{productoId}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable Long productoId) {
        log.info("DELETE /v1/inventario/producto/{}", productoId);
        inventarioService.eliminarInventarioPorProducto(productoId);
        return ResponseEntity.noContent().build(); 
    }
}
