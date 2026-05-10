package cl.duoc.busqueda.mapper;

import org.mapstruct.Mapper;

import cl.duoc.busqueda.dto.BusquedaRequest;
import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.dto.EnvioDTO;
import cl.duoc.busqueda.model.Busqueda;

//Mapper automatico, veamos si no explota en mil pedazos
@Mapper(componentModel = "spring")
public interface BusquedaMapper {
    BusquedaResponse toResponse(EnvioDTO envio);

}
