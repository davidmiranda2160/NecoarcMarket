package cl.duoc.usuario.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import cl.duoc.usuario.dto.UsuarioRequest;
import cl.duoc.usuario.dto.UsuarioResponse;
import cl.duoc.usuario.mapper.UsuarioMapper;
import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.repository.UsuarioRespository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    
    private final UsuarioRespository usuarioRespository;

    private final UsuarioMapper usuarioMapper;

    public List<Usuario> listarUsuarios() {
        log.info("Solicitando el listado completo de todos los usuarios registrados");
        List<Usuario> usuarios = usuarioRespository.findAll();
        log.info("Se recuperaron {} usuarios con éxito de la base de datos", usuarios.size());
        return usuarios;
    }

    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        log.info("Intentando registrar un nuevo usuario con correo: {}", request.getCorreo());

        Usuario usuario = usuarioMapper.fromRequest(request);
        Usuario usuarioGuardado = usuarioRespository.save(usuario);

        log.info("Usuario creado correctamente con el id: {}", usuarioGuardado.getId());
        return usuarioMapper.toResponse(usuarioGuardado);
    }

    public UsuarioResponse obtenerUsuarioPorId(Long id) {
        log.info("Buscando informacion del usuario con el id: {}", id);

        Usuario usuario = usuarioRespository.findById(id)
                .orElseThrow(() -> {
                    log.warn("La consulta fallo: El usuario con el id {} no existe en el sistema", id);
                    return new NoSuchElementException("No se encontro el usuario con id: " + id);
                });

        log.info("Usuario con el id {} fue localizado", id);
        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest datosNuevos) {
        log.info("Buscando usuario con el id: {} para iniciar la actualizacion de datos", id);

        Usuario usuarioExistente = usuarioRespository.findById(id)
                .orElseThrow(() -> {
                    log.warn("La actualizacion fallo: El usuario con el id {} no se puede actualizar porque no existe", id);
                    return new NoSuchElementException("No existe el usuario " + id + " a actualizar");
                });

        if (datosNuevos.getNombre() != null) {
            usuarioExistente.setNombre(datosNuevos.getNombre());
        }
        if (datosNuevos.getApellidos() != null) {
            usuarioExistente.setApellidos(datosNuevos.getApellidos());
        }
        if (datosNuevos.getCorreo() != null) {
            usuarioExistente.setCorreo(datosNuevos.getCorreo());
        }
        if (datosNuevos.getDireccion() != null) {
            usuarioExistente.setDireccion(datosNuevos.getDireccion());
        }
        if (datosNuevos.getTelefono() != null) {
            usuarioExistente.setTelefono(datosNuevos.getTelefono());
        }
        if (datosNuevos.getTipoUsuario() != null) {
            usuarioExistente.setTipoUsuario(datosNuevos.getTipoUsuario());
        }

        Usuario usuarioActualizado = usuarioRespository.save(usuarioExistente);
        log.info("Los datos del usuario con el id {} fueron actualizados correctamente en la base de datos", id);

        return usuarioMapper.toResponse(usuarioActualizado);
    }

    public void eliminarUsuarioPorId(Long id) {
        log.info("Intentando eliminar de la base de datos al usuario con id: {}", id);

        if (!usuarioRespository.existsById(id)) {
            log.warn("La eliminacion fallo: No se pudo encontrar al usuario con id: {} para realizar el borrado", id);
            throw new NoSuchElementException("No se pudo encontrar al usuario con id: " + id + " para eliminar");
        }

        usuarioRespository.deleteById(id);
        log.info("Usuario con el id {} eliminado correctamente del sistema", id);
    }

}
