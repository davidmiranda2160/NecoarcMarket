package cl.duoc.resena.mapper;

import org.springframework.stereotype.Component;
import cl.duoc.resena.dto.ResenaRequest;
import cl.duoc.resena.dto.ResenaResponse;
import cl.duoc.resena.model.Resena;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Component
@Builder
public class ResenaMapper {

    public Resena fromRequest(ResenaRequest request) {
        return Resena.builder()
            .productoId(request.getProductoId())
            .usuarioId(request.getUsuarioId())
            .calificacion(request.getCalificacion())
            .comentario(request.getComentario())
            .fechaCreacion(LocalDateTime.now())
            .build();
    }


    public ResenaResponse toResponse(Resena resena, String nombreUsuario) {
        return ResenaResponse.builder()
            .id(resena.getId()) 
            .productoId(resena.getProductoId())
            .nombreUsuario(nombreUsuario) 
            .calificacion(resena.getCalificacion())
            .comentario(resena.getComentario())
            .fechaCreacion(resena.getFechaCreacion().toString())
            .build();
    }
}
