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
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/v1/usuarios")
@Slf4j
public class UsuarioController {
    @Autowired UsuarioService usuarioService;


    @GetMapping("/{id}")
    public UsuarioResponse buscarUsuarioPorId(@PathVariable Long id) {
        log.info("");
        return usuarioService.obtenerUsuarioPorId(id);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        log.info("");
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarPaciente(@PathVariable Long id,
        @Valid @RequestBody UsuarioRequest request) {
        log.info("", id);
        return ResponseEntity.ok().body(usuarioService.actualizarUsuario(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        log.info("", id);
        usuarioService.eliminarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    //Falta listar usuarios
    @GetMapping()
    public List<Usuario> listarUsuarios(){
        log.info("GET /api/usuarios/listarUsuarios");
        return usuarioService.listarUsuarios();
    }
    

}