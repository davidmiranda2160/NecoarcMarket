package cl.duoc.usuario;

import java.util.List;

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

import cl.duoc.usuario.dto.UsuarioRequest;
import cl.duoc.usuario.dto.UsuarioResponse;
import cl.duoc.usuario.mapper.UsuarioMapper;
import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.repository.UsuarioRepository;
import cl.duoc.usuario.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Debería retornar la lista completa de usuarios con todos sus atributos intactos al invocar al servicio")
    void debeListarUsuariosExitosamente() {
    
    Usuario usuario1 = new Usuario();
    usuario1.setId(1L);
    usuario1.setNombre("Esteban");
    usuario1.setAppaterno("Quinto");
    usuario1.setApmaterno("Gonzales");
    usuario1.setCorreo("esteban.quinto@test.cl");
    usuario1.setDireccion("Calle Falsa 123");
    usuario1.setTelefono("+569978996777");
    usuario1.setTipoUsuario("Cliente");

    Usuario usuario2 = new Usuario();
    usuario2.setId(2L);
    usuario2.setNombre("Pedro");
    usuario2.setAppaterno("Perez");
    usuario2.setApmaterno("Gonzales");
    usuario2.setCorreo("angela.anhuaman@test.cl");
    usuario2.setDireccion("Avenida Siempre Viva 742");
    usuario2.setTelefono("+569912345678");
    usuario2.setTipoUsuario("Vendedor");

    List<Usuario> listaSimulada = List.of(usuario1, usuario2);

    when(usuarioRepository.findAll()).thenReturn(listaSimulada);

    List<Usuario> resultado = usuarioService.listarUsuarios();

    assertNotNull(resultado, "La lista de resultado no debería ser nula");
    assertEquals(2, resultado.size(), "El tamaño de la lista debería ser exactamente 2");

    Usuario resultado1 = resultado.get(0);
    assertEquals(1L, resultado1.getId(), "El ID del primer usuario debería ser 1");
    assertEquals("Esteban", resultado1.getNombre());
    assertEquals("Quinto", resultado1.getAppaterno());
    assertEquals("Gonzales", resultado1.getApmaterno());
    assertEquals("esteban.quinto@test.cl", resultado1.getCorreo());
    assertEquals("Calle Falsa 123", resultado1.getDireccion());
    assertEquals("+569978996777", resultado1.getTelefono());
    assertEquals("Cliente", resultado1.getTipoUsuario());

    Usuario resultado2 = resultado.get(1);
    assertEquals(2L, resultado2.getId(), "El id del segundo usuario deberia ser 2");
    assertEquals("Pedro", resultado2.getNombre());
    assertEquals("Perez", resultado2.getAppaterno());
    assertEquals("Gonzales", resultado2.getApmaterno());
    assertEquals("angela.anhuaman@test.cl", resultado2.getCorreo());
    assertEquals("Avenida Siempre Viva 742", resultado2.getDireccion());
    assertEquals("+569912345678", resultado2.getTelefono());
    assertEquals("Vendedor", resultado2.getTipoUsuario());

    verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería registrar y retornar el usuario correctamente cuando el correo electrónico no está duplicado")
    void debeCrearUsuarioExitosamente() {
    
        UsuarioRequest request = new UsuarioRequest();
        request.setNombre("Esteban");
        request.setAppaterno("Quinto");
        request.setApmaterno("Gonzales");
        request.setCorreo("esteban.quinto@test.cl");
        request.setDireccion("Calle Falsa 123");
        request.setTelefono("+569978996777");
        request.setTipoUsuario("Cliente");

        Usuario usuarioMapeado = new Usuario();
        usuarioMapeado.setNombre(request.getNombre());
        usuarioMapeado.setAppaterno(request.getAppaterno());
        usuarioMapeado.setApmaterno(request.getApmaterno());
        usuarioMapeado.setCorreo(request.getCorreo());
        usuarioMapeado.setDireccion(request.getDireccion());
        usuarioMapeado.setTelefono(request.getTelefono());
        usuarioMapeado.setTipoUsuario(request.getTipoUsuario());

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(10L); 
        usuarioGuardado.setNombre(request.getNombre());
        usuarioGuardado.setAppaterno(request.getAppaterno());
        usuarioGuardado.setApmaterno(request.getApmaterno());
        usuarioGuardado.setCorreo(request.getCorreo());
        usuarioGuardado.setDireccion(request.getDireccion());
        usuarioGuardado.setTelefono(request.getTelefono());
        usuarioGuardado.setTipoUsuario(request.getTipoUsuario());

        UsuarioResponse responseSimulado = new UsuarioResponse();
        responseSimulado.setId(10L);
        responseSimulado.setNombre("Esteban");
        responseSimulado.setCorreo("esteban.quinto@test.cl");

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(false);

        when(usuarioMapper.fromRequest(request)).thenReturn(usuarioMapeado);

        when(usuarioRepository.save(usuarioMapeado)).thenReturn(usuarioGuardado);

        when(usuarioMapper.toResponse(usuarioGuardado)).thenReturn(responseSimulado);

        UsuarioResponse resultado = usuarioService.crearUsuario(request);

        assertNotNull(resultado, "La respuesta no debería ser nula");
        assertEquals(10L, resultado.getId(), "El ID retornado debería ser 10");
        assertEquals("Esteban", resultado.getNombre());
        assertEquals("esteban.quinto@test.cl", resultado.getCorreo());

        verify(usuarioRepository, times(1)).existsByCorreo(request.getCorreo());
        verify(usuarioMapper, times(1)).fromRequest(request);
        verify(usuarioRepository, times(1)).save(usuarioMapeado);
        verify(usuarioMapper, times(1)).toResponse(usuarioGuardado);
    }

   @Test
    @DisplayName("Debería lanzar IllegalArgumentException y no guardar nada cuando el correo ya existe en el sistema")
    void debeLanzarExcepcionCuandoCorreoYaExiste() {

        UsuarioRequest request = new UsuarioRequest();
        request.setCorreo("Pedro.perez@test.cl");
        request.setNombre("Pedro");

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(true);

        
        IllegalArgumentException excepcionIdéntica = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario(request);
        }, "Se esperaba un IllegalArgumentException debido al correo duplicado");

        // Validamos el mensaje de error directamente sin usar bloques try-catch
        assertEquals("El correo electrónico ya se encuentra registrado en el sistema", excepcionIdéntica.getMessage());

        verify(usuarioRepository, times(1)).existsByCorreo(request.getCorreo());
        
        verify(usuarioMapper, never()).fromRequest(any(UsuarioRequest.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    
    
}
