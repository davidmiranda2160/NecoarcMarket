package cl.duoc.ordenes.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
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
import cl.duoc.ordenes.dto.OrdenesUpdateRequest;
import cl.duoc.ordenes.dto.PagosRequest;
import cl.duoc.ordenes.dto.PagosResponse;
import cl.duoc.ordenes.dto.UsuarioResponse;
import cl.duoc.ordenes.mapper.OrdenesMapper;
import cl.duoc.ordenes.model.Ordenes;
import cl.duoc.ordenes.repository.OrdenesRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public OrdenesResponse crearOrden(OrdenesRequest request) {
        log.info("Iniciando proceso de orden para usuario: {}", request.getIdUsuario());

        CarritoResponse carrito = carritoClient.buscarCarritoPorUsuarioId(request.getIdUsuario());

        if (carrito.getMontoTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("No se puede crear una orden con monto cero.");
        }

        UsuarioResponse usuario = usuarioClient.obtenerUsuarioPorId(request.getIdUsuario());

        Ordenes orden = ordenesMapper.fromRequest(request, carrito.getMontoTotal());
        orden.setEstadoOrden("PENDIENTE");
        orden = ordenesRepository.save(orden);

        PagosRequest pagosRequest = new PagosRequest();
        pagosRequest.setMetodoPago(request.getMetodoPago());
        pagosRequest.setMontoAPagar(carrito.getMontoTotal().doubleValue());

        PagosResponse pagosResponse = pagosClient.procesarPago(pagosRequest);

        // 5. Generar Envío
        EnvioRequest envioRequest = new EnvioRequest();
        envioRequest.setEmpresaTransporte(request.getEmpresaTransporte());
        envioRequest.setDireccionDestino(request.getDireccionEnvio());

        EnvioResponse envioResponse = envioClient.crearEnvio(envioRequest);

        orden.setIdPago(pagosResponse.getId());
        orden.setIdEnvio(envioResponse.getId());
        orden.setEstadoOrden("PAGADO");
        Ordenes ordenFinalizada = ordenesRepository.save(orden);

        carritoClient.vaciarCarrito(request.getIdUsuario());

        log.info("Orden {} creada y pagada exitosamente", ordenFinalizada.getId());
        return ordenesMapper.toResponse(ordenFinalizada, usuario, envioResponse, pagosResponse);
    }

    public OrdenesResponse obtenerPorId(Long id) {
        Ordenes orden = ordenesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("La orden con ID " + id + " no existe."));
        UsuarioResponse usuario = usuarioClient.obtenerUsuarioPorId(orden.getIdUsuario());
        PagosResponse pagos = pagosClient.obtenerPagoPorOrden(orden.getId());
        EnvioResponse envio = envioClient.obtenerEnvioPorId(orden.getIdEnvio());

        return ordenesMapper.toResponse(orden, usuario, envio, pagos);
    }

    public List<OrdenesResponse> listarTodas() {
        return ordenesRepository.findAll().stream()
                .map(orden -> ordenesMapper.toResponse(orden, null, null, null))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean eliminarOrden(Long id) {
        if (!ordenesRepository.existsById(id)) {
            throw new NoSuchElementException("No se puede eliminar: La orden " + id + " no existe.");
        }
        ordenesRepository.deleteById(id);
        return true;
    }

    public List<OrdenesResponse> listarPorUsuario(Long idUsuario) {
        return ordenesRepository.findByIdUsuario(idUsuario).stream()
                .map(orden -> {
                    UsuarioResponse usuario = usuarioClient.obtenerUsuarioPorId(orden.getIdUsuario());
                    return ordenesMapper.toResponse(orden, usuario, null, null);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public OrdenesResponse actualizarOrden(Long id, OrdenesUpdateRequest request) {

        Ordenes ordenExistente = ordenesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("La orden con ID " + id + " no existe."));

        ordenExistente.setDireccionEnvio(request.getDireccionEnvio());

        if (request.getEmpresaTransporte() != null) {
            log.info("Actualizando empresa de transporte a: {}", request.getEmpresaTransporte());
        }

        Ordenes guardada = ordenesRepository.save(ordenExistente);

        return ordenesMapper.toResponse(guardada, null, null, null);
    }
}
