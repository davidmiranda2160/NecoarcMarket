package cl.duoc.notificacion;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.notificacion.client.UsuarioClient;
import cl.duoc.notificacion.dto.NotificacionRequest;
import cl.duoc.notificacion.dto.NotificacionResponse;
import cl.duoc.notificacion.dto.UsuarioResponse;
import cl.duoc.notificacion.mapper.NotificacionMapper;
import cl.duoc.notificacion.model.Notificacion;
import cl.duoc.notificacion.repository.NotificacionRepository;
import cl.duoc.notificacion.service.NotificacionService;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private NotificacionMapper notificacionMapper;

    @InjectMocks
    private NotificacionService notificacionService;



    @Test
    @DisplayName("Debería retornar el historial de notificaciones mapeado con el nombre completo del usuario")
    void debeListarPorUsuarioExitosamente() {

        // GIVEN

        Long userId = 1L;
        UsuarioResponse userMock = new UsuarioResponse();
        userMock.setNombre("Esteban");
        userMock.setAppaterno("Quinto");

        Notificacion n1 = new Notificacion();
        n1.setUsuarioId(userId);
        n1.setTipo("EMAIL");

        NotificacionResponse responseMock = new NotificacionResponse();
        responseMock.setTipo("EMAIL");
        responseMock.setNombreUsuario("Esteban Quinto");

        when(usuarioClient.obtenerDatosUsuario(userId)).thenReturn(userMock);
        when(notificacionRepository.findByUsuarioIdOrderByFechaEnvioDesc(userId)).thenReturn(List.of(n1));
        when(notificacionMapper.toResponse(n1, "Esteban Quinto")).thenReturn(responseMock);

        // WHEN

        List<NotificacionResponse> resultado = notificacionService.listarPorUsuario(userId);


        // THEN

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Esteban Quinto", resultado.get(0).getNombreUsuario());
        verify(notificacionRepository, times(1)).findByUsuarioIdOrderByFechaEnvioDesc(userId);
    }

    @Test
    @DisplayName("Debería listar notificaciones como 'Usuario Desconocido' si el cliente de usuarios responde nulo")
    void debeListarComoUsuarioDesconocidoCuandoClienteFalla() {

        // GIVEN

        Long userId = 99L;
        Notificacion n1 = new Notificacion();

        when(usuarioClient.obtenerDatosUsuario(userId)).thenReturn(null);
        when(notificacionRepository.findByUsuarioIdOrderByFechaEnvioDesc(userId)).thenReturn(List.of(n1));
        
        NotificacionResponse responseMock = new NotificacionResponse();
        responseMock.setNombreUsuario("Usuario Desconocido");
        
        when(notificacionMapper.toResponse(n1, "Usuario Desconocido")).thenReturn(responseMock);

        // WHEN

        List<NotificacionResponse> resultado = notificacionService.listarPorUsuario(userId);


        // THEN

        assertNotNull(resultado);
        assertEquals("Usuario Desconocido", resultado.get(0).getNombreUsuario());
    }


    @Test
    @DisplayName("Debería crear una notificación personalizada con un saludo cuando el usuario existe")
    void debeCrearNotificacionConUsuarioExistente() {

        // GIVEN
    
        NotificacionRequest request = new NotificacionRequest();
        request.setUsuarioId(1L);
        request.setTipo("ALERTA");
        request.setMensaje("Tu pedido va en camino");

        UsuarioResponse userMock = new UsuarioResponse();
        userMock.setNombre("Esteban");
        userMock.setApmaperno("Quinto"); 

        when(usuarioClient.obtenerDatosUsuario(1L)).thenReturn(userMock);


        ArgumentCaptor<Notificacion> notificacionCaptor = ArgumentCaptor.forClass(Notificacion.class);


        // WHEN
        notificacionService.crear(request);

        // THEN
        verify(notificacionRepository, times(1)).save(notificacionCaptor.capture());
        Notificacion notificacionGuardada = notificacionCaptor.getValue();

        assertNotNull(notificacionGuardada);
        assertEquals("ALERTA", notificacionGuardada.getTipo());
        assertEquals("Hola Esteban Quinto: Tu pedido va en camino", notificacionGuardada.getMensaje());
    }

    @Test
    @DisplayName("Debería crear una notificación con mensaje genérico de aviso si el usuario es nulo")
    void debeCrearNotificacionConMensajeGenericoSiUsuarioEsNulo() {

        // GIVEN

        NotificacionRequest request = new NotificacionRequest();
        request.setUsuarioId(99L);
        request.setTipo("SMS");
        request.setMensaje("Stock bajo");

        when(usuarioClient.obtenerDatosUsuario(99L)).thenReturn(null);
        ArgumentCaptor<Notificacion> notificacionCaptor = ArgumentCaptor.forClass(Notificacion.class);

        // WHEN

        notificacionService.crear(request);

        // THEN

        verify(notificacionRepository, times(1)).save(notificacionCaptor.capture());
        Notificacion notificacionGuardada = notificacionCaptor.getValue();

        assertNotNull(notificacionGuardada);
        assertEquals("Aviso para Usuario ID 99: Stock bajo", notificacionGuardada.getMensaje());
    }
}
