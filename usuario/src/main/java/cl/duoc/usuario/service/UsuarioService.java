package cl.duoc.usuario.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.usuario.dto.UsuarioRequest;
import cl.duoc.usuario.dto.UsuarioResponse;
import cl.duoc.usuario.mapper.UsuarioMapper;
import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.repository.UsuarioRespository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRespository usuarioRespository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    public List<Usuario> listarUsuarios(){
        log.info("Buscando todos los usuarios");
        return usuarioRespository.findAll();
    }
    
    public UsuarioResponse crearUsuario(UsuarioRequest request){
        log.info("Intentando crear al usuario con correo: {}", request.getCorreo());
        
        Usuario usuario = usuarioMapper.fromRequest(request);
        Usuario usuarioGuardado = usuarioRespository.save(usuario);
        
        log.info("Usuario creado exitosamente");
        return usuarioMapper.toResponse(usuarioGuardado);
    }

    public UsuarioResponse obtenerUsuarioPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRespository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con id: " + id));
        
        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest datosNuevos) {
        log.info("Buscando usuario con ID: {} para actualizar", id);
        
        Usuario usuarioExistente = usuarioRespository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el usuario " + id + " a actualizar"));

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
        log.info("Usuario ID {} actualizado correctamente", id);

        return usuarioMapper.toResponse(usuarioActualizado);
    }

    public void eliminarUsuarioPorId(Long id) {
        log.info("Intentando eliminar usuario con ID: {}", id);
        
        if (!usuarioRespository.existsById(id)) {
            throw new NoSuchElementException("No se pudo encontrar al usuario con id: " + id + " para eliminar");
        }
        
        usuarioRespository.deleteById(id);
        log.info("Usuario eliminado correctamente");
    }
}