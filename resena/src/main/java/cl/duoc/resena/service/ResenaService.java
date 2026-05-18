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
import lombok.extern.slf4j.Slf4j;
import cl.duoc.resena.dto.UsuarioResponse; 

@Service
@Slf4j
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private ResenaMapper resenaMapper;

    @Autowired
    private UsuarioClient usuarioClient;

    public ResenaResponse crear(ResenaRequest request) {
        log.info("Inicio de creación de reseña para el producto ID {}", request.getProductoId());
        Resena resena = resenaMapper.fromRequest(request);
        Resena guardada = resenaRepository.save(resena);
        String nombreParaMostrar = obtenerNombreDeUsuario(guardada.getUsuarioId());
            return resenaMapper.toResponse(guardada, nombreParaMostrar);
    }

    public List<ResenaResponse> listarPorProducto(Long productoId) {
        log.info("Buscando todas las reseñas asociadas al producto ID: {}", productoId);
        return resenaRepository.findByProductoId(productoId).stream()
                .map(resena -> {
                    String nombre = obtenerNombreDeUsuario(resena.getUsuarioId());
                    return resenaMapper.toResponse(resena, nombre);
                })
                .collect(Collectors.toList());
    }

    //Tanto problema y solo era el null de la fecha
    private String obtenerNombreDeUsuario(Long usuarioId) {
        try {
            UsuarioResponse user = usuarioClient.obtenerDatosUsuario(usuarioId);
            if (user == null || user.getNombre() == null) {
                log.warn("El micro de Usuarios devolvió un usuario nulo o sin nombre para el ID: {}", usuarioId);
                return "Usuario Desconocido";
            }
            return user.getNombre() + " " + (user.getApellidos() != null ? user.getApellidos() : "");
        } catch (Exception e) {
            log.error("Error al intentar obtener el nombre usuario (vamos a dejar vivo a David): {}", e.getMessage());
            return "Servicio no disponible";
        }
    }
}
