package cl.duoc.pagos.controller;

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

import cl.duoc.pagos.dto.PagosRequest;
import cl.duoc.pagos.dto.PagosResponse;
import cl.duoc.pagos.service.PagosService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("v1/pagos")
@Slf4j
public class PagosController {

    @Autowired
    private PagosService pagosService;

    @GetMapping
    public ResponseEntity<List<PagosResponse>> listarTodos() {
        log.info("Petición recibida para listar todos los pagos");
        return ResponseEntity.ok(pagosService.listarTodosLosPagos());
    }

    @GetMapping("/orden/{id}")
    public ResponseEntity<PagosResponse> obtenerPagoPorIdOrden(@PathVariable("id") Long id) {
        log.info("Buscando pago asociado a la orden ID: {}", id);
        return ResponseEntity.ok(pagosService.obtenerPagoPorOrden(id));
    }

    @PostMapping("/crear")
    public ResponseEntity<PagosResponse> procesarPago(@Valid @RequestBody PagosRequest request) {
        log.info("Procesando nuevo pago para la orden: {}", request.getIdOrden());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagosService.procesarPago(request));
    }

}
