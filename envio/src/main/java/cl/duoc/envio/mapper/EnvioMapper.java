package cl.duoc.envio.mapper;

import org.springframework.stereotype.Component;

import cl.duoc.envio.dto.EnvioRequest;
import cl.duoc.envio.dto.EnvioResponse;
import cl.duoc.envio.dto.OrdenesResponse;
import cl.duoc.envio.model.Envio;

@Component
public class EnvioMapper {

    public Envio fromRequest(EnvioRequest request) {
        if (request == null){
        return null;
    }

        return Envio.builder()
                .empresaTransporte(request.getEmpresaTransporte())
                .direccionDestino(request.getDireccionDestino())
                .build();
    }

    public EnvioResponse toResponse(Envio envio, OrdenesResponse ordenResponse) {
        if (envio == null) {
            return null;
        }

        return EnvioResponse.builder()
                .id(envio.getId())
                .codigoSeguimiento(envio.getCodigoSeguimiento())
                .estadoEnvio(envio.getEstadoEnvio())
                .empresaTransporte(envio.getEmpresaTransporte())
                .fechaEstimadaEntrega(envio.getFechaEstimadaEntrega())
                .direccionDestino(envio.getDireccionDestino())
                .orden(ordenResponse)
                .build();

    }
}
