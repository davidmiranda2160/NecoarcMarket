package cl.duoc.notificacion.mapper;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import cl.duoc.notificacion.dto.NotificacionResponse;
import cl.duoc.notificacion.model.Notificacion;

@Component
public class NotificacionMapper {

    public NotificacionResponse toResponse(Notificacion entity) {
        if (entity == null) return null;

        NotificacionResponse res = new NotificacionResponse();
        res.setId(entity.getId());
        res.setMensaje(entity.getMensaje());
        res.setTipo(entity.getTipo());
        return res;
    }

}