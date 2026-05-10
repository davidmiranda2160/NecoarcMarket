package cl.duoc.pagos.service;

import java.util.NoSuchElementException;

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

    public PagosResponse crearPago(PagosRequest request) {
        try {
            Pagos pago = pagosMapper.fromRequest(request);
            Pagos pagoGuardado = pagosRepository.save(pago);

            log.info("Pago realizado");
            return pagosMapper.toResponse(pagoGuardado);

        } catch (Exception e) {
            log.error("Error al crear el registro de pago: {}", e.getMessage());
            throw new RuntimeException("No se pudo procesar el pago");
        }
    }

    public PagosResponse obtenerPagoPorPedido(Long idPedido) {
        Pagos pago = pagosRepository.findByIdPedido(idPedido)
                .orElseThrow(() -> new NoSuchElementException("No se encontro un pago para el id: " + idPedido));

        return pagosMapper.toResponse(pago);
    }

    public PagosResponse actualizarPago(Long id, PagosRequest request) {

        Pagos pago = pagosRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se puede actualizar el pago: " + id));

        pago.setMetodoPago(request.getMetodoPago());

        Pagos actualizado = pagosRepository.save(pago);
        log.info("Pago actualizado correctamente");
        return pagosMapper.toResponse(actualizado);
    }

    public void eliminarPago(Long id) {
        if (!pagosRepository.existsById(id)) {
            throw new NoSuchElementException("No se puede eliminar: " + id);
        }

        pagosRepository.deleteById(id);
        log.warn("Se ha eliminado el registro de pago con id: ", id);
    }

}
