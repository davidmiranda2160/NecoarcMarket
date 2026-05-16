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
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.ordenes.dto.OrdenesUpdateRequest;
import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.service.OrdenesService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("v1/ordenes")
public class OrdenesController {

    @Autowired
    private OrdenesService ordenesService;

    @PostMapping
    public ResponseEntity<OrdenesResponse> crear(@Valid @RequestBody OrdenesRequest request) {
        return new ResponseEntity<>(ordenesService.crearOrden(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrdenesResponse>> listarTodas() {
        return ResponseEntity.ok(ordenesService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenesResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenesService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<OrdenesResponse>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(ordenesService.listarPorUsuario(idUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenesResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrdenesUpdateRequest request) {

        return ResponseEntity.ok(ordenesService.actualizarOrden(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (ordenesService.eliminarOrden(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
