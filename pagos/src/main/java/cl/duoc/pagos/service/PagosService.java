package cl.duoc.pagos.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.duoc.pagos.Client.OrdenesClient;
import cl.duoc.pagos.dto.PagosRequest;
import cl.duoc.pagos.dto.PagosResponse;
import cl.duoc.pagos.mapper.PagosMapper;
import cl.duoc.pagos.model.Pagos;
import cl.duoc.pagos.repository.PagosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PagosService {

    private final PagosRepository pagosRepository;

    private final OrdenesClient ordenesClient;

    private final PagosMapper pagosMapper;

    public PagosResponse procesarPago(PagosRequest request) {
        log.info("Iniciando procesamiento de pago para la orden con id: {}", request.getIdOrden());

        if (pagosRepository.existsByIdOrden(request.getIdOrden())) {
            log.warn("Intento de duplicar pago rechazado: La orden con id {} ya cuenta con un pago", request.getIdOrden());
            throw new IllegalStateException("Ya existe un pago registrado para la orden: " + request.getIdOrden());
        }

        String estadoFinalPago;
        String estadoOrdenSincronizar;

        if ("RECHAZADO".equalsIgnoreCase(request.getMetodoPago())) {
            log.warn("Simulación de pasarela: Transacción rechazada por la entidad bancaria para Orden ID: {}", request.getIdOrden());
            estadoFinalPago = "RECHAZADO";
            estadoOrdenSincronizar = "Cancelada";
        } else {
            log.info("Simulación de pasarela: Transacción aprobada con éxito.");
            estadoFinalPago = "APROBADO";
            estadoOrdenSincronizar = "Pagada";
        }

        Pagos pago = pagosMapper.fromRequest(request);
        pago.setMontoPagado(request.getMontoAPagar()); 
        pago.setEstadoPago(estadoFinalPago);     
        pago.setFechaTransaccion(java.time.LocalDateTime.now());

        Pagos pagoGuardado = pagosRepository.save(pago);
        log.info("El pago se registró localmente con ID: {} y estado: {}", pagoGuardado.getId(), estadoFinalPago);


        log.info("Enviando estado '{}' al microservicio de Órdenes para ID: {}", estadoOrdenSincronizar, request.getIdOrden());
        ordenesClient.actualizarEstadoOrden(request.getIdOrden(), estadoOrdenSincronizar);

        return pagosMapper.toResponse(pagoGuardado);
    }

    public PagosResponse obtenerPagoPorOrden(Long idOrden) {
        log.info("Buscando registro de pago asociado a la orden con id: {}", idOrden);

        Pagos pago = pagosRepository.findByIdOrden(idOrden)
                .orElseThrow(() -> {
                    log.warn("La consulta fallo: No se encontro ningun pago para la orden con id: {}", idOrden);
                    return new NoSuchElementException("No se encontró un pago para la orden: " + idOrden);
                });

        log.info("Registro de pago localizado con éxito para la orden ID: {}", idOrden);
        return pagosMapper.toResponse(pago);
    }

    public List<PagosResponse> listarTodosLosPagos() {
        log.info("Solicitando el listado completo de transacciones de pago realizadas");
        List<Pagos> pagos = pagosRepository.findAll();

        log.info("Se recuperaron con exito {} registros de pago de la base de datos", pagos.size());
        return pagos.stream()
                .map(pagosMapper::toResponse)
                .collect(Collectors.toList());
    }
}
