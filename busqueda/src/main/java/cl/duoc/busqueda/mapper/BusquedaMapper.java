package cl.duoc.busqueda.mapper;

import org.mapstruct.Mapper;

import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.dto.EnvioDTO;

@Mapper(componentModel = "spring")
public interface BusquedaMapper {
    BusquedaResponse toResponse(EnvioDTO envio);
}
