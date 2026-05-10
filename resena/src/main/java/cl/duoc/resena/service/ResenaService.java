package cl.duoc.resena.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.resena.client.UsuarioClient; 
import cl.duoc.resena.dto.ResenaRequest;
import cl.duoc.resena.dto.ResenaResponse;
import cl.duoc.resena.mapper.ResenaMapper;
import cl.duoc.resena.model.Resena;
import cl.duoc.resena.repository.ResenaRepository;
import cl.duoc.resena.dto.UsuarioResponse; 

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private ResenaMapper resenaMapper;

    @Autowired
    private UsuarioClient usuarioClient;

    public ResenaResponse crear(ResenaRequest request) {
        Resena resena = resenaMapper.fromRequest(request);
        Resena guardada = resenaRepository.save(resena);


        String nombreParaMostrar = obtenerNombreDeDavid(guardada.getUsuarioId());


        return resenaMapper.toResponse(guardada, nombreParaMostrar);
    }

    public List<ResenaResponse> listarPorProducto(Long productoId) {
        return resenaRepository.findByProductoId(productoId).stream()
                .map(resena -> {
                    String nombre = obtenerNombreDeDavid(resena.getUsuarioId());
                    return resenaMapper.toResponse(resena, nombre);
                })
                .collect(Collectors.toList());
    }


    private String obtenerNombreDeDavid(Long usuarioId) {
        try {
            UsuarioResponse user = usuarioClient.obtenerDatosUsuario(usuarioId);
            return (user != null) ? user.getNombre() + " " + user.getApellidos() : "Usuario Desconocido";
        } catch (Exception e) {
            return "Servicio de Usuarios no disponible";
        }
    }
}
