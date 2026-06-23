package cl.duoc.busqueda.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.busqueda.dto.BusquedaRequest;
import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.model.Busqueda;



@Component
public class BusquedaMapper {
    public Busqueda fromRequest(BusquedaRequest request){
        return Busqueda.builder()
                .codigoSeguimiento(request.getCodigoSeguimiento())
                .envioId(request.getEnvioId())
                .estadoEnvio(request.getEstadoEnvio())
                .detalle(request.getDetalle())
                .nombreProducto(request.getNombreProducto())
                .fechaActualizacionProducto(request.getFechaActualizacionProducto())
                .build();
    }
    public BusquedaResponse toResponse(Busqueda busqueda, String estadoNombre){
        return BusquedaResponse.builder()
                .id(busqueda.getId())
                .codigoSeguimiento(busqueda.getCodigoSeguimiento())
                .envioId(busqueda.getEnvioId())    
                .estadoEnvio(estadoNombre)
                .detalle(busqueda.getDetalle())
                .nombreProducto(busqueda.getNombreProducto()) 
                .fechaActualizacionProducto(busqueda.getFechaActualizacionProducto())
                .build();
        }
}




