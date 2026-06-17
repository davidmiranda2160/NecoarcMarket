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

import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.service.OrdenesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/ordenes")
@RequiredArgsConstructor
public class OrdenesController {

    private final OrdenesService ordenesService;

    /* Esta es la linea gatilla todo el flujo que se espera de este servicio
    (Carrito -> Orden -> Pago -> Limpiar Carrito)
     */
    @PostMapping
    public ResponseEntity<OrdenesResponse> crearOrden(@Valid @RequestBody OrdenesRequest request) {
        return new ResponseEntity<>(ordenesService.crearOrden(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrdenesResponse>> listarTodas() {
        return ResponseEntity.ok(ordenesService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenesResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenesService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<OrdenesResponse>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ordenesService.obtenerPorUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<OrdenesResponse>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ordenesService.obtenerPorEstado(estado));
    }

    @PutMapping("/{idOrden}/estado")
    public ResponseEntity<Void> actualizarEstadoDesdePagos(
            @PathVariable Long idOrden,
            @RequestParam String estado) {

        ordenesService.actualizarEstadoDesdePagos(idOrden, estado);
        return ResponseEntity.noContent().build();
    }
}
