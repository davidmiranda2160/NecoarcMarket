package cl.duoc.usuario.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.usuario.dto.UsuarioRequest;
import cl.duoc.usuario.dto.UsuarioResponse;
import cl.duoc.usuario.model.Usuario;

@Component
public class UsuarioMapper {

     public Usuario fromRequest(UsuarioRequest request) {
        return Usuario.builder()
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .direccion(request.getDireccion())
                .correo(request.getCorreo())
                .telefono(request.getTelefono())
                .tipoUsuario(request.getTipoUsuario())
                .build();
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .direccion(usuario.getDireccion())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .tipoUsuario(usuario.getTipoUsuario())
                .build();
    }

}
