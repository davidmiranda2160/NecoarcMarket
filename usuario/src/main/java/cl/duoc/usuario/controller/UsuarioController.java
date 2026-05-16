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
    UsuarioService usuarioService;

    @GetMapping("/{id}")
    public UsuarioResponse buscarUsuarioPorId(@PathVariable Long id) {
        log.info("POST /v1/usuario");
        return usuarioService.obtenerUsuarioPorId(id);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        log.info("POST /v1/usuario - Intentando crear usuario");
        UsuarioResponse response = usuarioService.crearUsuario(request);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(@PathVariable Long id,
        @Valid @RequestBody UsuarioRequest request) {
        log.info("PUT /v1/usuario/{}", id);
        return ResponseEntity.ok().body(usuarioService.actualizarUsuario(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("DELETE /v1/usuario/{}", id);
        usuarioService.eliminarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    //Falta listar usuarios
    @GetMapping()
    public List<Usuario> listarUsuarios() {
        log.info("GET /v1/usuarios/listarUsuarios");
        return usuarioService.listarUsuarios();
    }
}