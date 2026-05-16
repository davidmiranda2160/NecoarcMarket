package cl.duoc.ordenes.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import cl.duoc.ordenes.dto.EnvioResponse;
import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.dto.PagosResponse;
import cl.duoc.ordenes.dto.UsuarioResponse;
import cl.duoc.ordenes.model.Ordenes;

@Component
public class OrdenesMapper {

     public Ordenes fromRequest(OrdenesRequest request, BigDecimal montoTotal) {
        return Ordenes.builder()
                .idUsuario(request.getIdUsuario())
                .direccionEnvio(request.getDireccionEnvio())
                .montoTotal(montoTotal)
                .estadoOrden("Pendiente")
                .build();
    }

    public OrdenesResponse toResponse(Ordenes ordenes, UsuarioResponse usuario,
        EnvioResponse envio, PagosResponse pagos
    ) {
        return OrdenesResponse.builder()
                .id(ordenes.getId())
                .idUsuario(ordenes.getIdUsuario())
                .fechaCreacion(ordenes.getFechaCreacion())
                .estadoOrden(ordenes.getEstadoOrden())
                .montoTotal(ordenes.getMontoTotal())
                .direccionEnvio(ordenes.getDireccionEnvio())
                .usuario(usuario)
                .envio(envio)
                .pagos(pagos)
                .build();
    }
}
