package cl.duoc.ordenes.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.ordenes.client.CarritoClient;
import cl.duoc.ordenes.client.EnvioClient;
import cl.duoc.ordenes.client.PagosClient;
import cl.duoc.ordenes.client.UsuarioClient;
import cl.duoc.ordenes.dto.CarritoResponse;
import cl.duoc.ordenes.dto.EnvioRequest;
import cl.duoc.ordenes.dto.EnvioResponse;
import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.dto.PagosRequest;
import cl.duoc.ordenes.dto.PagosResponse;
import cl.duoc.ordenes.dto.UsuarioResponse;
import cl.duoc.ordenes.mapper.OrdenesMapper;
import cl.duoc.ordenes.model.Ordenes;
import cl.duoc.ordenes.repository.OrdenesRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenesService {

    @Autowired
    private OrdenesRepository ordenesRepository;

    @Autowired
    private CarritoClient carritoClient;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private PagosClient pagosClient;

    @Autowired
    private EnvioClient envioClient;

    @Autowired
    private OrdenesMapper ordenesMapper;

    public OrdenesResponse crearOrden(OrdenesRequest request) {
        log.info("Inciando proceso para crear la orden para el usuario {}", request.getIdUsuario());

        CarritoResponse carrito = carritoClient.buscarUsuarioPorId(request.getIdUsuario());
        if (carrito == null || carrito.getCantidad() == 0) {
            log.warn("el carrito del usuario {} esta vacio. no se puede procesar", request.getIdUsuario());
            return null;
        }

        int montoTotal = carrito.getMontoTotal();

        UsuarioResponse usuario = usuarioClient.obtenerUsuarioPorId(request.getIdUsuario());

        //En esta parte registramos la orden en la base con el estado incial como pendiente
        Ordenes ordenEstadoInicial = ordenesMapper.fromRequest(request, montoTotal);
        Ordenes ordenGuardada = ordenesRepository.save(ordenEstadoInicial);

        PagosRequest pagosRequest = new PagosRequest();
        pagosRequest.setMetodoPago(request.getMetodoPago());
        pagosRequest.setMontoAPagar(montoTotal);
        PagosResponse pagosResponse = pagosClient.procesarPago(pagosRequest);

        EnvioRequest envioRequest = new EnvioRequest();
        envioRequest.setEmpresaTransporte(request.getEmpresaTransporte());
        envioRequest.setDireccionDestino(request.getDireccionEnvio());
        EnvioResponse envioResponse = envioClient.crearEnvio(envioRequest);

        ordenGuardada.setIdPago(pagosResponse.getId());
        ordenGuardada.setEstadoOrden("Pagado");
        Ordenes orden = ordenesRepository.save(ordenGuardada);

        return ordenesMapper.toResponse(orden, usuario, envioResponse, pagosResponse);
    }

    public List<OrdenesResponse> listarTodas() {
        return ordenesRepository.findAll().stream()
                .map(orden -> ordenesMapper.toResponse(orden, null, null, null))
                .collect(Collectors.toList());
    }

    public OrdenesResponse obtenerPorId(Long id) {
        Optional<Ordenes> ordenBuscar = ordenesRepository.findById(id);

        if (ordenBuscar.isPresent()) {
            Ordenes orden = ordenBuscar.get();

            UsuarioResponse usuario = usuarioClient.obtenerUsuarioPorId(id);
            PagosResponse pagos = pagosClient.obtenerPagoPorOrden(orden.getId());
            EnvioResponse envio = envioClient.obtenerEnvioPorId(orden.getIdEnvio());
            return ordenesMapper.toResponse(orden, usuario, envio, pagos);
        }
        return null;
    }

    public OrdenesResponse actualizarOrden(Long id, OrdenesRequest request) {
        return ordenesRepository.findById(id).map(ordenExistente -> {
            ordenExistente.setIdUsuario(request.getIdUsuario());

            ordenExistente.setIdEnvio(request.getIdEnvio());

            Ordenes ordenGuardada = ordenesRepository.save(ordenExistente);

            UsuarioResponse usuario = usuarioClient.obtenerUsuarioPorId(ordenGuardada.getIdUsuario());
            PagosResponse pagos = pagosClient.obtenerPagoPorOrden(ordenGuardada.getId());
            EnvioResponse envio = envioClient.obtenerEnvioPorId(ordenGuardada.getIdEnvio());

            return ordenesMapper.toResponse(ordenGuardada, usuario, envio, pagos);

        }).orElse(null);
    }

    public boolean eliminarOrden(Long id) {
        if (ordenesRepository.existsById(id)) {
            ordenesRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<OrdenesResponse> listarPorUsuario(Long idUsuario) {
        return ordenesRepository.findByIdUsuario(idUsuario).stream()
                .map(orden -> {
                    UsuarioResponse usuario = usuarioClient.obtenerUsuarioPorId(orden.getIdUsuario());
                    PagosResponse pagos = pagosClient.obtenerPagoPorOrden(orden.getId());
                    EnvioResponse envio = envioClient.obtenerEnvioPorId(orden.getIdEnvio());

                    return ordenesMapper.toResponse(orden, usuario, envio, pagos);
                })
                .collect(Collectors.toList());
    }
}
