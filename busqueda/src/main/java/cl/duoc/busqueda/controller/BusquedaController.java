package cl.duoc.busqueda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.busqueda.dto.BusquedaRequest;
import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.service.BusquedaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/busqueda")
@Slf4j
public class BusquedaController {

    @Autowired
    private BusquedaService busquedaService;

    @PostMapping("/rastrear")
    public ResponseEntity<BusquedaResponse> rastrearPedido(@Valid @RequestBody BusquedaRequest request) {
    
        BusquedaResponse respuesta = busquedaService.obtenerSeguimientoCompleto(request.getCodigoSeguimiento());
        return ResponseEntity.ok(respuesta);
    }
}
