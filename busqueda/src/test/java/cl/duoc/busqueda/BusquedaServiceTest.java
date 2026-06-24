package cl.duoc.busqueda;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.busqueda.client.EnvioClient;
import cl.duoc.busqueda.dto.BusquedaRequest;
import cl.duoc.busqueda.dto.BusquedaResponse;
import cl.duoc.busqueda.dto.EnvioResponse;
import cl.duoc.busqueda.mapper.BusquedaMapper;
import cl.duoc.busqueda.model.Busqueda;
import cl.duoc.busqueda.repository.BusquedaRepository;
import cl.duoc.busqueda.service.BusquedaService;

@ExtendWith(MockitoExtension.class)
public class BusquedaServiceTest {

    @Mock
    private BusquedaRepository busquedaRepository;

    @Mock
    private EnvioClient envioClient;

    @Mock
    private BusquedaMapper busquedaMapper;

    @InjectMocks
    private BusquedaService busquedaService;

    // =========================================================================
    // TESTS PARA EL MÉTODO: obtenerSeguimientoCompleto()
    // =========================================================================

    @Test
    @DisplayName("Debería retornar el seguimiento actualizado cruzando datos con el microservicio de Envíos")
    void debeObtenerSeguimientoCompletoExitosamente() {
        // ------------------------------------------------------------------------
        // GIVEN
        // ------------------------------------------------------------------------
        String codigo = "TRACK-NECO-005";
        Busqueda busquedaLocal = Busqueda.builder()
                .id(1L)
                .codigoSeguimiento(codigo)
                .envioId(10L)
                .estadoEnvio("en bodega")
                .build();

        EnvioResponse envioExterno = new EnvioResponse();
        envioExterno.setEstadoEnvio("en camino");

        BusquedaResponse responseMock = BusquedaResponse.builder()
                .codigoSeguimiento(codigo)
                .estadoEnvio("en camino")
                .build();

        when(busquedaRepository.findByCodigoSeguimiento(codigo)).thenReturn(Optional.of(busquedaLocal));
        when(envioClient.consultarEstado(10L)).thenReturn(envioExterno);
        when(busquedaRepository.save(any(Busqueda.class))).thenReturn(busquedaLocal);
        when(busquedaMapper.toResponse(busquedaLocal, "en camino")).thenReturn(responseMock);

        // ------------------------------------------------------------------------
        // WHEN
        // ------------------------------------------------------------------------
        BusquedaResponse resultado = busquedaService.obtenerSeguimientoCompleto(codigo);

        // ------------------------------------------------------------------------
        // THEN
        // ------------------------------------------------------------------------
        assertNotNull(resultado);
        assertEquals("en camino", resultado.getEstadoEnvio());
        verify(busquedaRepository, times(1)).save(any(Busqueda.class));
        verify(envioClient, times(1)).consultarEstado(10L);
    }

    @Test
    @DisplayName("Debería retornar el estado local de respaldo si la conexión con Envíos falla")
    void debeObtenerSeguimientoUsandoEstadoLocalCuandoEnviosSeCae() {
        // ------------------------------------------------------------------------
        // GIVEN
        // ------------------------------------------------------------------------
        String codigo = "TRACK-NECO-005";
        Busqueda busquedaLocal = Busqueda.builder()
                .id(1L)
                .codigoSeguimiento(codigo)
                .envioId(10L)
                .estadoEnvio("en bodega")
                .build();

        BusquedaResponse responseMock = BusquedaResponse.builder()
                .codigoSeguimiento(codigo)
                .estadoEnvio("en bodega")
                .build();

        when(busquedaRepository.findByCodigoSeguimiento(codigo)).thenReturn(Optional.of(busquedaLocal));
        // Simulamos la caída del micro de Envíos (el caso de David desconectado)
        when(envioClient.consultarEstado(10L)).thenThrow(new RuntimeException("Connection refused"));
        when(busquedaMapper.toResponse(busquedaLocal, "en bodega")).thenReturn(responseMock);

        // ------------------------------------------------------------------------
        // WHEN
        // ------------------------------------------------------------------------
        BusquedaResponse resultado = busquedaService.obtenerSeguimientoCompleto(codigo);

        // ------------------------------------------------------------------------
        // THEN
        // ------------------------------------------------------------------------
        assertNotNull(resultado);
        assertEquals("en bodega", resultado.getEstadoEnvio());
        // Verificamos que NUNCA intentó guardar en BD porque no hubo cambios de estado
        verify(busquedaRepository, never()).save(any(Busqueda.class));
    }

    @Test
    @DisplayName("Debería lanzar NoSuchElementException si el código de seguimiento no existe en la base de datos")
    void debeLanzarExcepcionCuandoCodigoNoEstaRegistrado() {
        // ------------------------------------------------------------------------
        // GIVEN
        // ------------------------------------------------------------------------
        String codigoInexistente = "TRACK-NOT-FOUND";
        when(busquedaRepository.findByCodigoSeguimiento(codigoInexistente)).thenReturn(Optional.empty());

        // ------------------------------------------------------------------------
        // WHEN & THEN
        // ------------------------------------------------------------------------
        assertThrows(NoSuchElementException.class, () -> {
            busquedaService.obtenerSeguimientoCompleto(codigoInexistente);
        });
    }

    // =========================================================================
    // TESTS PARA EL MÉTODO: registrarNuevoSeguimiento()
    // =========================================================================

    @Test
    @DisplayName("Debería registrar un nuevo seguimiento correctamente")
    void debeRegistrarNuevoSeguimientoExitosamente() {
        // ------------------------------------------------------------------------
        // GIVEN
        // ------------------------------------------------------------------------
        BusquedaRequest request = new BusquedaRequest();
        request.setCodigoSeguimiento("TRACK-NECO-005");
        request.setEstadoEnvio("en camino");

        Busqueda nuevaBusqueda = Busqueda.builder().codigoSeguimiento("TRACK-NECO-005").estadoEnvio("en camino").build();
        Busqueda busquedaGuardada = Busqueda.builder().id(1L).codigoSeguimiento("TRACK-NECO-005").estadoEnvio("en camino").build();
        BusquedaResponse responseMock = BusquedaResponse.builder().id(1L).codigoSeguimiento("TRACK-NECO-005").estadoEnvio("en camino").build();

        when(busquedaMapper.fromRequest(request)).thenReturn(nuevaBusqueda);
        when(busquedaRepository.save(nuevaBusqueda)).thenReturn(busquedaGuardada);
        when(busquedaMapper.toResponse(busquedaGuardada, "en camino")).thenReturn(responseMock);

        // ------------------------------------------------------------------------
        // WHEN
        // ------------------------------------------------------------------------
        BusquedaResponse resultado = busquedaService.registrarNuevoSeguimiento(request);

        // ------------------------------------------------------------------------
        // THEN
        // ------------------------------------------------------------------------
        assertNotNull(resultado);
        assertEquals("TRACK-NECO-005", resultado.getCodigoSeguimiento());
        verify(busquedaRepository, times(1)).save(nuevaBusqueda);
    }
}
