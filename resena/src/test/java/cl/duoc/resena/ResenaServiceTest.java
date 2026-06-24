package cl.duoc.resena;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.resena.client.UsuarioClient;
import cl.duoc.resena.dto.ResenaRequest;
import cl.duoc.resena.dto.ResenaResponse;
import cl.duoc.resena.dto.UsuarioResponse;
import cl.duoc.resena.mapper.ResenaMapper;
import cl.duoc.resena.model.Resena;
import cl.duoc.resena.repository.ResenaRepository;
import cl.duoc.resena.service.ResenaService;

@ExtendWith(MockitoExtension.class)
public class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private ResenaMapper resenaMapper;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ResenaService resenaService;


    @Test
    @DisplayName("Debería crear una reseña exitosamente obteniendo el nombre completo del usuario")
    void debeCrearResenaExitosamente() {

        // GIVEN

        ResenaRequest request = new ResenaRequest();
        request.setProductoId(1L);
        request.setUsuarioId(10L);

        Resena resenaMapeada = Resena.builder().productoId(1L).usuarioId(10L).build();
        Resena resenaGuardada = Resena.builder().id(100L).productoId(1L).usuarioId(10L).build();
        
        UsuarioResponse userMock = new UsuarioResponse();
        userMock.setNombre("Esteban");
        userMock.setApellidos("Quinto");

        ResenaResponse responseMock = ResenaResponse.builder()
                .id(100L)
                .nombreUsuario("Esteban Quinto")
                .build();

        when(resenaMapper.fromRequest(request)).thenReturn(resenaMapeada);
        when(resenaRepository.save(resenaMapeada)).thenReturn(resenaGuardada);
        when(usuarioClient.obtenerDatosUsuario(10L)).thenReturn(userMock);
        when(resenaMapper.toResponse(resenaGuardada, "Esteban Quinto")).thenReturn(responseMock);


        // WHEN

        ResenaResponse resultado = resenaService.crear(request);


        // THEN
        assertNotNull(resultado);
        assertEquals("Esteban Quinto", resultado.getNombreUsuario());
        verify(resenaRepository, times(1)).save(resenaMapeada);
        verify(usuarioClient, times(1)).obtenerDatosUsuario(10L);
    }

    @Test
    @DisplayName("Debería registrar la reseña como 'Servicio no disponible' si el micro de usuarios falla")
    void debeCrearResenaConFallbackCuandoUsuariosSeCae() {

        // GIVEN

        ResenaRequest request = new ResenaRequest();
        request.setProductoId(1L);
        request.setUsuarioId(10L);

        Resena resenaMapeada = Resena.builder().productoId(1L).usuarioId(10L).build();
        Resena resenaGuardada = Resena.builder().id(100L).productoId(1L).usuarioId(10L).build();

        ResenaResponse responseMock = ResenaResponse.builder()
                .id(100L)
                .nombreUsuario("Servicio no disponible")
                .build();

        when(resenaMapper.fromRequest(request)).thenReturn(resenaMapeada);
        when(resenaRepository.save(resenaMapeada)).thenReturn(resenaGuardada);
        

        when(usuarioClient.obtenerDatosUsuario(10L)).thenThrow(new RuntimeException("Timeout Connection"));
        when(resenaMapper.toResponse(resenaGuardada, "Servicio no disponible")).thenReturn(responseMock);

        // WHEN

        ResenaResponse resultado = resenaService.crear(request);

        // THEN

        assertNotNull(resultado);
        assertEquals("Servicio no disponible", resultado.getNombreUsuario());
    }


    @Test
    @DisplayName("Debería listar las reseñas de un producto y colocar 'Usuario Desconocido' si la API responde vacío")
    void debeListarResenasConUsuarioDesconocido() {

        Long prodId = 1L;
        Resena r1 = Resena.builder().id(100L).productoId(prodId).usuarioId(20L).build();
        
        // El cliente responde un objeto vacío o null
        when(resenaRepository.findByProductoId(prodId)).thenReturn(List.of(r1));
        when(usuarioClient.obtenerDatosUsuario(20L)).thenReturn(null); 
        
        ResenaResponse responseMock = ResenaResponse.builder()
                .id(100L)
                .nombreUsuario("Usuario Desconocido")
                .build();
                
        when(resenaMapper.toResponse(r1, "Usuario Desconocido")).thenReturn(responseMock);


        // WHEN

        List<ResenaResponse> resultado = resenaService.listarPorProducto(prodId);


        // THEN

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Usuario Desconocido", resultado.get(0).getNombreUsuario());
        verify(resenaRepository, times(1)).findByProductoId(prodId);
    }
}