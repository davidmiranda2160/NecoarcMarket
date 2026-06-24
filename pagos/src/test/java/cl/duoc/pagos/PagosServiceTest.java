package cl.duoc.pagos;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.pagos.Client.OrdenesClient;
import cl.duoc.pagos.dto.PagosRequest;
import cl.duoc.pagos.dto.PagosResponse;
import cl.duoc.pagos.mapper.PagosMapper;
import cl.duoc.pagos.model.Pagos;
import cl.duoc.pagos.repository.PagosRepository;
import cl.duoc.pagos.service.PagosService;

@ExtendWith(MockitoExtension.class)
public class PagosServiceTest {

    @Mock private PagosRepository pagosRepository;
    @Mock private OrdenesClient ordenesClient;
    @Mock private PagosMapper pagosMapper;

    @InjectMocks private PagosService pagosService;

    @Test
    @DisplayName("Debería procesar correctamente un pago aprobado sincronizando el estado con el microservicio de órdenes")
    void cuandoProcesarPagoEsExitoso_entoncesRegistraPagoYOrdenPasaAPagada() {
        PagosRequest request = new PagosRequest();
        request.setIdOrden(5L);
        request.setMontoAPagar(BigDecimal.valueOf(207.92));
        request.setMetodoPago("WEBPAY_CREDITO");

        Pagos pago = new Pagos();
        pago.setIdOrden(5L);

        Pagos pagoGuardado = new Pagos();
        pagoGuardado.setId(10L);
        pagoGuardado.setIdOrden(5L);
        pagoGuardado.setEstadoPago("APROBADO");

        PagosResponse responseEsperada = new PagosResponse();
        responseEsperada.setId(10L);
        responseEsperada.setIdOrden(5L);
        responseEsperada.setEstadoPago("APROBADO");
        responseEsperada.setMontoAPagar(BigDecimal.valueOf(207.92));

        when(pagosRepository.existsByIdOrden(5L)).thenReturn(false);
        when(pagosMapper.fromRequest(any(PagosRequest.class))).thenReturn(pago);
        when(pagosRepository.save(any(Pagos.class))).thenReturn(pagoGuardado);
        when(pagosMapper.toResponse(any(Pagos.class))).thenReturn(responseEsperada);

        PagosResponse resultado = pagosService.procesarPago(request);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(5L, resultado.getIdOrden());
        assertEquals("APROBADO", resultado.getEstadoPago());
        assertEquals(207.92, resultado.getMontoPagado());

        verify(pagosRepository, times(1)).save(pago);
        verify(ordenesClient, times(1)).actualizarEstadoOrden(5L, "Pagada");
    }

    @Test
    @DisplayName("Debería rechazar el pago simulado cuando el método es RECHAZADO y actualizar el estado de la orden a Cancelada")
    void cuandoMetodoPagoEsRechazado_entoncesRegistraRechazoYOrdenPasaACancelada() {
        PagosRequest request = new PagosRequest();
        request.setIdOrden(5L);
        request.setMontoAPagar(BigDecimal.valueOf(207.92));
        request.setMetodoPago("RECHAZADO");

        Pagos pago = new Pagos();
        pago.setIdOrden(5L);

        Pagos pagoGuardado = new Pagos();
        pagoGuardado.setId(10L);
        pagoGuardado.setIdOrden(5L);
        pagoGuardado.setEstadoPago("RECHAZADO");

        PagosResponse responseEsperada = new PagosResponse();
        responseEsperada.setId(10L);
        responseEsperada.setIdOrden(5L);
        responseEsperada.setEstadoPago("RECHAZADO");

        when(pagosRepository.existsByIdOrden(5L)).thenReturn(false);
        when(pagosMapper.fromRequest(any(PagosRequest.class))).thenReturn(pago);
        when(pagosRepository.save(any(Pagos.class))).thenReturn(pagoGuardado);
        when(pagosMapper.toResponse(any(Pagos.class))).thenReturn(responseEsperada);

        PagosResponse resultado = pagosService.procesarPago(request);

        assertNotNull(resultado);
        assertEquals("RECHAZADO", resultado.getEstadoPago());
        verify(pagosRepository, times(1)).save(pago);
        verify(ordenesClient, times(1)).actualizarEstadoOrden(5L, "Cancelada");
    }

    @Test
    @DisplayName("Debería lanzar IllegalStateException al intentar procesar un pago si la orden ya tiene un pago registrado")
    void cuandoProcesarPagoDuplicado_entoncesLanzaIllegalStateException() {
        PagosRequest request = new PagosRequest();
        request.setIdOrden(5L);

        when(pagosRepository.existsByIdOrden(5L)).thenReturn(true);

        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> {
            pagosService.procesarPago(request);
        });

        assertEquals("Ya existe un pago registrado para la orden: 5", excepcion.getMessage());
        verify(pagosRepository, never()).save(any(Pagos.class));
        verify(ordenesClient, never()).actualizarEstadoOrden(any(Long.class), any(String.class));
    }

    @Test
    @DisplayName("Debería retornar el pago correspondiente cuando se consulta por un ID de orden existente")
    void cuandoObtenerPagoPorOrdenExistente_entoncesDevuelvePagosResponse() {
        Long idOrden = 5L;
        Pagos pago = new Pagos();
        pago.setId(10L);
        pago.setIdOrden(idOrden);

        PagosResponse responseEsperada = new PagosResponse();
        responseEsperada.setId(10L);
        responseEsperada.setIdOrden(idOrden);

        when(pagosRepository.findByIdOrden(idOrden)).thenReturn(Optional.of(pago));
        when(pagosMapper.toResponse(pago)).thenReturn(responseEsperada);

        PagosResponse resultado = pagosService.obtenerPagoPorOrden(idOrden);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(5L, resultado.getIdOrden());
        verify(pagosRepository, times(1)).findByIdOrden(idOrden);
    }

    @Test
    @DisplayName("Debería lanzar NoSuchElementException al consultar el pago de una orden que no existe en el sistema")
    void cuandoObtenerPagoPorOrdenInexistente_entoncesLanzaNoSuchElementException() {
        Long idInexistente = 99L;
        when(pagosRepository.findByIdOrden(idInexistente)).thenReturn(Optional.empty());

        NoSuchElementException excepcion = assertThrows(NoSuchElementException.class, () -> {
            pagosService.obtenerPagoPorOrden(idInexistente);
        });

        assertEquals("No se encontró un pago para la orden: " + idInexistente, excepcion.getMessage());
        verify(pagosRepository, times(1)).findByIdOrden(idInexistente);
    }

    @Test
    @DisplayName("Debería retornar la lista completa de todas las transacciones de pago realizadas")
    void cuandoListarTodosLosPagos_entoncesDevuelveListaDePagos() {
        Pagos pago1 = new Pagos();
        pago1.setId(10L);

        PagosResponse responseEsperada = new PagosResponse();
        responseEsperada.setId(10L);

        when(pagosRepository.findAll()).thenReturn(List.of(pago1));
        when(pagosMapper.toResponse(any(Pagos.class))).thenReturn(responseEsperada);

        List<PagosResponse> resultado = pagosService.listarTodosLosPagos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getId());
        verify(pagosRepository, times(1)).findAll();
    }
}
