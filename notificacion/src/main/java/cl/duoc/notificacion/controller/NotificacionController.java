package cl.duoc.notificacion.controller;

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

import cl.duoc.notificacion.dto.NotificacionRequest;
import cl.duoc.notificacion.dto.NotificacionResponse;
import cl.duoc.notificacion.service.NotificacionService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/notificaciones")
@Slf4j
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    //crear notificaciones que probablemente usen otros servicios
    @PostMapping
    public ResponseEntity<String> enviar(@RequestBody NotificacionRequest request) {
        log.info("POST /v1/notificaciones - creando norificacion");
        notificacionService.crear(request);
        return new ResponseEntity<>("Notificación creada con éxito", HttpStatus.CREATED);
    }

    //ver notificaciones del usuario (cambie el antiguo)
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<NotificacionResponse>> listar(@PathVariable Long id) {
        log.info("GET /v1/notificaciones/usuario/{} - ver notificación del usuario", id);
        List<NotificacionResponse> lista = notificacionService.listarPorUsuario(id);
        return ResponseEntity.ok(lista);
    }
}
