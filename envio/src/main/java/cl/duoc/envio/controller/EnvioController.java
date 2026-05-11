package cl.duoc.envio.controller;

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

import cl.duoc.envio.dto.EnvioRequest;
import cl.duoc.envio.dto.EnvioResponse;
import cl.duoc.envio.service.EnvioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/envio")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    // Creamos un envío asociado a una orden
    @PostMapping("/orden/{ordenId}")
    public ResponseEntity<EnvioResponse> crearEnvio(
            @Valid @RequestBody EnvioRequest request, 
            @PathVariable Long ordenId) {
        
        EnvioResponse response = envioService.crearEnvio(request, ordenId);
        
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        
        return ResponseEntity.badRequest().build();
    }

    //Lista todos los envíos
    @GetMapping
    public ResponseEntity<List<EnvioResponse>> listarTodos() {
        return ResponseEntity.ok(envioService.listarTodos());
    }

    // Obtenemos un envío por medio de su id
    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponse> obtenerPorId(@PathVariable Long id) {
        EnvioResponse response = envioService.obtenerPorId(id);
        
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }
}
