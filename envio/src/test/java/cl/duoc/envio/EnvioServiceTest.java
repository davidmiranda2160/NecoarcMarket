package cl.duoc.envio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.envio.client.OrdenesClient;
import cl.duoc.envio.dto.EnvioRequest;
import cl.duoc.envio.dto.EnvioResponse;
import cl.duoc.envio.dto.OrdenesResponse;
import cl.duoc.envio.mapper.EnvioMapper;
import cl.duoc.envio.model.Envio;
import cl.duoc.envio.repository.EnvioRepository;
import cl.duoc.envio.service.EnvioService;

@ExtendWith(MockitoExtension.class)
public class EnvioServiceTest {

    @Mock private EnvioRepository envioRepository;
    @Mock private EnvioMapper envioMapper;
    @Mock private OrdenesClient ordenesClient;

    @InjectMocks
    private EnvioService envioService;

    
    private EnvioResponse crearEnvioResponseSimulado() {
        EnvioResponse response = new EnvioResponse();
        response.setId(5L);
        response.setCodigoSeguimiento("TRACK-NECO-005");
        response.setEstadoEnvio("Preparando");
        response.setEmpresaTransporte("Chile Express");
        response.setFechaEstimadaEntrega(java.time.LocalDate.of(2026, 6, 27));
        response.setDireccionDestino("Calle falsa 123");
        response.setFechaCreacion(LocalDateTime.of(2026, 6, 24, 6, 22, 12));
        
        OrdenesResponse orden = new OrdenesResponse();
        orden.setId(5L);
        orden.setUsuarioId(5L);
        orden.setTotal(BigDecimal.valueOf(129.95));
        orden.setEstadoOrden("Pagada");
        response.setOrden(orden);
        return response;
    }

    @Test
    @DisplayName("Debería registrar y retornar el envío con la estructura de datos correcta")
    void debeCrearEnvioExitosamente() {
        EnvioRequest request = new EnvioRequest();
        request.setOrdenId(5L);
        
        Envio envio = new Envio();
        envio.setId(5L);
        
        OrdenesResponse orden = new OrdenesResponse();
        orden.setId(5L);

        when(envioRepository.existsByOrdenId(5L)).thenReturn(false);
        when(ordenesClient.buscarOrdenPorId(5L)).thenReturn(orden);
        when(envioMapper.fromRequest(request)).thenReturn(envio);
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        
        EnvioResponse responseSimulado = crearEnvioResponseSimulado();
        when(envioMapper.toResponse(any(Envio.class), eq(orden))).thenReturn(responseSimulado);

        EnvioResponse resultado = envioService.crearEnvio(request);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals("TRACK-NECO-005", resultado.getCodigoSeguimiento());
        assertEquals("Preparando", resultado.getEstadoEnvio());
        assertEquals(5L, resultado.getOrden().getId());
        
        verify(envioRepository, times(1)).save(any(Envio.class));
    }

    @Test
    @DisplayName("Debería actualizar el estado a ENTREGADO correctamente validando los datos")
    void debeActualizarEstadoExitosamente() {
        Long id = 5L;
        Envio envio = new Envio();
        envio.setId(id);
        envio.setEstadoEnvio("Preparando");
        
        OrdenesResponse orden = new OrdenesResponse();
        orden.setId(5L);

        when(envioRepository.findById(id)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        when(ordenesClient.buscarOrdenPorId(any())).thenReturn(orden);
        
        EnvioResponse responseSimulado = crearEnvioResponseSimulado();
        responseSimulado.setEstadoEnvio("ENTREGADO");
        when(envioMapper.toResponse(any(Envio.class), any())).thenReturn(responseSimulado);

        EnvioResponse resultado = envioService.actualizarEstado(id, "ENTREGADO");

        assertEquals("ENTREGADO", resultado.getEstadoEnvio());
        verify(envioRepository, times(1)).save(envio);
    }

    @Test
    @DisplayName("Debería lanzar NoSuchElementException si el id del envio no existe al actualizar")
    void debeLanzarExcepcionSiEnvioNoExisteAlActualizar() {
        Long id = 99L;
        when(envioRepository.findById(id)).thenReturn(Optional.empty());

        NoSuchElementException excepcion = assertThrows(NoSuchElementException.class, () -> {
            envioService.actualizarEstado(id, "EN_CAMINO");
        });

        assertEquals("No se puede actualizar el estado porque no existe el envio con id: " + id, excepcion.getMessage());
        
        verify(envioRepository, never()).save(any(Envio.class));
    }
}