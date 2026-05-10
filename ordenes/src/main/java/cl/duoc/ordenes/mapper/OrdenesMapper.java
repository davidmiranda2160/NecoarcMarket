package cl.duoc.ordenes.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.model.Ordenes;

@Component
public class OrdenesMapper {

     public Ordenes fromRequest(OrdenesRequest request) {
        return Ordenes.builder()
                .idUsuario(request.getIdUsuario())
                .direccionEnvio(request.getDireccionEnvio())
                .build();
    }

    public OrdenesResponse toResponse(Ordenes ordenes) {
        return OrdenesResponse.builder()
                .id(ordenes.getId())
                .idUsuario(ordenes.getIdUsuario())
                .fechaCreacion(ordenes.getFechaCreacion())
                .estadoOrden(ordenes.getEstadoOrden())
                .montoTotal(ordenes.getMontoTotal())
                .direccionEnvio(ordenes.getDireccionEnvio())
                .idPago(ordenes.getIdPago())
                .build();
    }

}
