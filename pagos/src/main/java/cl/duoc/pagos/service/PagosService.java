package cl.duoc.pagos.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.pagos.dto.PagosRequest;
import cl.duoc.pagos.dto.PagosResponse;
import cl.duoc.pagos.mapper.PagosMapper;
import cl.duoc.pagos.model.Pagos;
import cl.duoc.pagos.repository.PagosRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PagosService {

    @Autowired
    private PagosRepository pagosRepository;

    @Autowired
    private PagosMapper pagosMapper;

    public PagosResponse procesarPago(PagosRequest request) {
        try {
            if (pagosRepository.existsByIdOrden(request.getIdOrden())) {
                log.warn("Intento de duplicar pago para la orden ID: {}", request.getIdOrden());
                throw new IllegalStateException("Ya existe un pago registrado para la orden: " + request.getIdOrden());
            }

            Pagos pago = pagosMapper.fromRequest(request);
            Pagos pagoGuardado = pagosRepository.save(pago);

            log.info("Pago procesado exitosamente para la orden: {}", request.getIdOrden());
            return pagosMapper.toResponse(pagoGuardado);

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al crear el registro de pago: {}", e.getMessage());
            throw new RuntimeException("No se pudo procesar el pago");
        }
    }

    public PagosResponse obtenerPagoPorOrden(Long idOrden) {
        log.info("Buscando pago asociado a la orden: {}", idOrden);
        Pagos pago = pagosRepository.findByIdOrden(idOrden)
                .orElseThrow(() -> new NoSuchElementException("No se encontró un pago para la orden: " + idOrden));

        return pagosMapper.toResponse(pago);
    }

    public List<PagosResponse> listarTodosLosPagos() {
        log.info("Obteniendo todos los registros de pago de la base de datos");
        return pagosRepository.findAll()
                .stream()
                .map(pagosMapper::toResponse)
                .collect(Collectors.toList());
    }
}
