package cl.duoc.usuario.controller;

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

import cl.duoc.usuario.dto.PagosRequest;
import cl.duoc.usuario.dto.PagosResponse;
import cl.duoc.usuario.dto.UsuarioRequest;
import cl.duoc.usuario.dto.UsuarioResponse;
import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/usuario")
@Slf4j
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(@PathVariable Long id) {
        log.info("GET /v1/usuario/{}", id);
        UsuarioResponse response = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        log.info("POST /v1/usuario - Intentando crear usuario");

        UsuarioResponse response = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(@PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        log.info("PUT /v1/usuario/{}", id);
        UsuarioResponse response = usuarioService.actualizarUsuario(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("DELETE /v1/usuario/{}", id);
        usuarioService.eliminarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        log.info("GET /v1/usuario - Listando todos los usuarios");
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<PagosResponse> pagar(@PathVariable Long id, @RequestBody PagosRequest request) {
        log.info("POST /v1/usuario/{}/pagos - Solicitando pago al microservicio", id);
        return ResponseEntity.ok(usuarioService.solicitarPago(id, request));
    }

    @GetMapping("/reporte-pagos")
    public ResponseEntity<List<PagosResponse>> verTodosLosPagos() {
        log.info("GET /v1/usuario/pagos-reporte - Obteniendo historial global");
        return ResponseEntity.ok(usuarioService.obtenerHistorialPagos());
    }
}