package cl.duoc.notificacion.service;

import java.time.LocalDateTime;

import java.util.List;
//pa ver si me ahorro el mapper, igual lo voy a poner
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.notificacion.client.UsuarioClient;
import cl.duoc.notificacion.dto.NotificacionRequest;
import cl.duoc.notificacion.dto.NotificacionResponse;
import cl.duoc.notificacion.dto.UsuarioResponse;
import cl.duoc.notificacion.mapper.NotificacionMapper;
import cl.duoc.notificacion.model.Notificacion;
import cl.duoc.notificacion.repository.NotificacionRepository;


@Service
public class NotificacionService {

    @Autowired
    private UsuarioClient usuarioClient;
    
    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private NotificacionMapper notificacionMapper;


    public List<NotificacionResponse> listarPorUsuario(Long id) {
        return notificacionRepository.findByUsuarioIdOrderByFechaEnvioDesc(id)
                .stream()
                .map(notificacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void crear(NotificacionRequest request) {
        //microservicio del ingeniebro david
        UsuarioResponse user = usuarioClient.obtenerDatosUsuario(request.getUsuarioId());
        
        Notificacion n = new Notificacion();
        n.setUsuarioId(request.getUsuarioId());
        n.setTipo(request.getTipo());
        n.setFechaEnvio(LocalDateTime.now());
        if(user != null) {
            String saludo = "Hola " + user.getNombre() + " " + user.getApellidos();
            n.setMensaje(saludo + ": " + request.getMensaje());
        } else {
            n.setMensaje("Aviso para Usuario ID " + request.getUsuarioId() + ": " + request.getMensaje());
        }
        notificacionRepository.save(n);
    }
}
