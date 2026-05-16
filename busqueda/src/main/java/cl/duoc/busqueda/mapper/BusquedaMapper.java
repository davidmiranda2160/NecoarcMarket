package cl.duoc.busqueda.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.busqueda.dto.BusquedaRequest;
import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.model.Busqueda;

//Mapper automatico, veamos si no explota en mil pedazos

@Component
public class BusquedaMapper {
    public Busqueda fromRequest(BusquedaRequest request){
        return Busqueda.builder()
                .codigoSeguimiento(request.getCodigoSeguimiento())
                .envioId(request.getEnvioId())
                .estadoEnvio(request.getEstadoEnvio())
                .detalle(request.getDetalle())
                .fechaActualizacionProducto(java.time.LocalDateTime.now())
                .build();
    }
    public BusquedaResponse toResponse(Busqueda busqueda, String nombre){
            return BusquedaResponse.builder()
                    .id(busqueda.getId())
                    .codigoSeguimiento(busqueda.getCodigoSeguimiento())
                    .envioId(busqueda.getEnvioId())    
                    .estadoEnvio(nombre)
                    .detalle(busqueda.getDetalle())
                    .fechaActualizacionProducto(busqueda.getFechaActualizacionProducto())
                    .build();
        }
}




