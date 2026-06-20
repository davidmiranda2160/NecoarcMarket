package cl.duoc.usuario.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.usuario.dto.UsuarioRequest;
import cl.duoc.usuario.dto.UsuarioResponse;
import cl.duoc.usuario.mapper.UsuarioMapper;
import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void dadoUnUsuarioRequestValido_cuandoSeCreaUsuario_entoncesRetornaUsuarioResponseExitoso(){
        UsuarioRequest request = new UsuarioRequest();
        request.setNombre("Pedrito");
        request.setCorreo("pedrito@test.cl");

        Usuario usuarioMapeado = new Usuario();
        usuarioMapeado.setNombre(request.getNombre());
        usuarioMapeado.setCorreo(request.getCorreo());

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1L);
        usuarioGuardado.setNombre(request.getNombre());
        usuarioGuardado.setCorreo(request.getCorreo());

        UsuarioResponse responseEsperado = new UsuarioResponse();
        responseEsperado.setId(1L);
        responseEsperado.setNombre("Pedrito");

        when(usuarioMapper.fromRequest(request)).thenReturn(usuarioMapeado);
        when(usuarioRepository.save(usuarioMapeado)).thenReturn(usuarioGuardado);
        when(usuarioMapper.toResponse(usuarioGuardado)).thenReturn(responseEsperado);

        UsuarioResponse resultado = usuarioService.crearUsuario(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        verify(usuarioMapper, times(1)).fromRequest(request);
        verify(usuarioRepository, times(1)).save(usuarioMapeado);
        verify(usuarioMapper, times(1)).toResponse(usuarioGuardado);
    }

    @Test
    void dadoUnIdExistente_CuandoSeBuscaUsuarioPorId_entoncesRetornaUsuarioResponse(){
       Long idExistente = 1L;
       Usuario usuarioEnBD = new Usuario();
       usuarioEnBD.setId(idExistente);
       usuarioEnBD.setNombre("Angela");

       UsuarioResponse responseEsperado = new UsuarioResponse();
       responseEsperado.setId(idExistente);
       responseEsperado.setNombre("Angela");

       when(usuarioRepository.findById(idExistente)).thenReturn(Optional.of(usuarioEnBD));
       when(usuarioMapper.toResponse(usuarioEnBD)).thenReturn(responseEsperado);

       UsuarioResponse resultado = usuarioService.obtenerUsuarioPorId(idExistente);

       assertNotNull(resultado);
       assertEquals(idExistente, resultado.getId());
       assertEquals("Angela", resultado.getNombre());
       verify(usuarioRepository, times(1)).findById(idExistente);
       verify(usuarioMapper, times(1)).toResponse(usuarioEnBD);
    }

    @Test
    void dadoUnIdYDatosNuevos_cuandoSeActualizaUsuario_entoncesRetornaUsuarioResponseActualizado() {
        Long idExistente = 1L;
        
        UsuarioRequest datosNuevos = new UsuarioRequest();
        datosNuevos.setNombre("Pedrito Modificado");
        datosNuevos.setCorreo("pedrito.nuevo@test.cl");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(idExistente);
        usuarioExistente.setNombre("Pedrito Original");
        usuarioExistente.setCorreo("pedrito@test.cl");

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(idExistente);
        usuarioActualizado.setNombre("Pedrito Modificado");
        usuarioActualizado.setCorreo("pedrito.nuevo@test.cl");

        UsuarioResponse responseEsperado = new UsuarioResponse();
        responseEsperado.setId(idExistente);
        responseEsperado.setNombre("Pedrito Modificado");

        when(usuarioRepository.findById(idExistente)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(usuarioExistente)).thenReturn(usuarioActualizado);
        when(usuarioMapper.toResponse(usuarioActualizado)).thenReturn(responseEsperado);

        UsuarioResponse resultado = usuarioService.actualizarUsuario(idExistente, datosNuevos);

        assertNotNull(resultado);
        assertEquals("Pedrito Modificado", resultado.getNombre());
        
        verify(usuarioRepository, times(1)).findById(idExistente);
        verify(usuarioRepository, times(1)).save(usuarioExistente);
        verify(usuarioMapper, times(1)).toResponse(usuarioActualizado);
    }

    @Test
    void dadoUnIdExistente_cuandoSeEliminaUsuario_entoncesInvocaDeleteById() {

        Long idExistente = 1L;

        when(usuarioRepository.existsById(idExistente)).thenReturn(true);
        
        usuarioService.eliminarUsuarioPorId(idExistente);

        verify(usuarioRepository, times(1)).existsById(idExistente);
        verify(usuarioRepository, times(1)).deleteById(idExistente);
    }
}
