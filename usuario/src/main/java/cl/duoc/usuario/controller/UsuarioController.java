package cl.duoc.usuario.controller;

import java.util.List;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/usuario")
@Slf4j
@RequiredArgsConstructor
@Tag(name= "Usuario", description= "Operaciones relacionadas con el microservicio de usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/{id}")
    @Operation(summary= "Obtener a un perfil", description= "Busca a un usuario mediante su ID")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description= "El usuario ha sido encontrado",
            content= @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode= "404", description= "El usuario no fue encontrado")
    })
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(@PathVariable Long id) {
        log.info("GET /v1/usuario/{}", id);
        UsuarioResponse response = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary= "Crear un perfil", description= "Crea un perfil para un usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "201", description= "El usuario fue creado",
            content= @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode= "404", description= "El usuario no se pudo crear")
    })
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        log.info("POST /v1/usuario - Intentando crear usuario");

        UsuarioResponse response = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary= "Actualizar a un perfil", description= "Actualizar el perfil de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description= "El usuario fue actualizado correctamente",
            content= @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode= "404", description= "El usuario no se pudo actualizar")
    })
    public ResponseEntity<UsuarioResponse> actualizarUsuario(@PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        log.info("PUT /v1/usuario/{}", id);
        UsuarioResponse response = usuarioService.actualizarUsuario(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary= "Eliminar un perfil", description= "Eliminar un perfil de un usuario existente en el sistema")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "204", description= "El usuario fue eliminado",
            content= @Content(mediaType = "application/json")),
        @ApiResponse(responseCode= "404", description= "El usuario no se pudo eliminar")
    })
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("DELETE /v1/usuario/{}", id);
        usuarioService.eliminarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary= "Lista a todos los perfiles", description= "Busca y trae a todos los usuarios del sistema")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description= "Usuarios fueron listados",
            content= @Content(mediaType = "application/json",
                array= @ArraySchema(schema = @Schema(implementation = Usuario.class)))),
        @ApiResponse(responseCode= "404", description= "No se pudo traer a los usuario")
    })
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        log.info("GET /v1/usuario - Listando todos los usuarios");
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

}