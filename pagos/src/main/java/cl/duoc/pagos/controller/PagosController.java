package cl.duoc.pagos.controller;

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

import cl.duoc.pagos.dto.PagosRequest;
import cl.duoc.pagos.dto.PagosResponse;
import cl.duoc.pagos.service.PagosService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/pagos")
@Slf4j
public class PagosController {

    @Autowired
    private PagosService pagosService;

    @GetMapping("/{id}")
    public PagosResponse obtenerCarritoPorUsuario(@PathVariable Long idPedido) {
        return pagosService.obtenerPagoPorPedido(idPedido);
    }

    @PostMapping
    public ResponseEntity<PagosResponse> agregarProducto(@Valid @RequestBody PagosRequest request) {
        log.info("");
        return ResponseEntity.status(HttpStatus.CREATED).body(pagosService.crearPago(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagosResponse> actualizarPaciente(@PathVariable Long id,
            @Valid @RequestBody PagosRequest request) {
        log.info("", id);
        return ResponseEntity
                .ok()
                .body(pagosService.actualizarPago(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        log.info("", id);
        pagosService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }

}
