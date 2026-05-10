package cl.duoc.resena.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.resena.dto.ResenaRequest;
import cl.duoc.resena.dto.ResenaResponse;
import cl.duoc.resena.service.ResenaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/resenas")
public class ResenaController {
    @Autowired private ResenaService resenaService;

    @PostMapping
    public ResponseEntity<ResenaResponse> guardar(@Valid @RequestBody ResenaRequest request) {
        return new ResponseEntity<>(resenaService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaResponse>> obtener(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.listarPorProducto(productoId));
    }
}
