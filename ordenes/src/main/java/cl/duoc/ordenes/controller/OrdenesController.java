package cl.duoc.ordenes.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.service.OrdenesService;

@RestController
@RequestMapping("/v1/ordenes")
public class OrdenesController {

    @Autowired
    private OrdenesService ordenesService;

    @PostMapping
    public ResponseEntity<OrdenesResponse> crearOrden(@RequestBody OrdenesRequest request) {
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

    @PutMapping("/{id}")
    public ResponseEntity<OrdenesResponse> actualizarEstado(
            @PathVariable Long id, 
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(ordenesService.actualizarEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ordenesService.eliminarOrden(id);
        return ResponseEntity.noContent().build();
    }
}