package cl.duoc.inventario.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.inventario.dto.InventarioRequest;
import cl.duoc.inventario.dto.InventarioResponse;
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

    @GetMapping("/producto/{productoId}")
public ResponseEntity<InventarioResponse> obtenerStock(@PathVariable Long productoId) {
    log.info("API GET: Consultando stock del producto {}", productoId);
    return ResponseEntity.ok(inventarioService.obtenerStockPorProducto(productoId));
}
    //Conexion microservicio pedidos para descontar, lo voy a añadir cuando hayan más microservicios por si hay que vincular la venta de usuario vendedor o vincularla a cliente
    //
}
