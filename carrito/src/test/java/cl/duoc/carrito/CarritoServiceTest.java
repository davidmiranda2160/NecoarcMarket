package cl.duoc.carrito;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.carrito.client.ProductoClient;
import cl.duoc.carrito.client.UsuarioClient;
import cl.duoc.carrito.dto.CarritoRequest;
import cl.duoc.carrito.dto.CarritoResponse;
import cl.duoc.carrito.dto.ProductoResponse;
import cl.duoc.carrito.dto.UsuarioResponse;
import cl.duoc.carrito.mapper.CarritoMapper;
import cl.duoc.carrito.model.Carrito;
import cl.duoc.carrito.repository.CarrritoRepository;
import cl.duoc.carrito.service.CarritoService;

@ExtendWith(MockitoExtension.class)
public class CarritoServiceTest {
   @Mock
    private CarrritoRepository carritoRepository;

    @Mock
    private CarritoMapper carritoMapper;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private CarritoService carritoService;

    @Test
    void agregarProducto_CuandoEsNuevo_DeberiaCalcularYGuardar() {
        // Los datos que se cargaran
        Long idUsuario = 5L;
        Long idProducto = 1L;

        // Mockear el dto de entrada es decir nuestro request
        CarritoRequest requestMock = new CarritoRequest();
        requestMock.setCantidad(5);

        UsuarioResponse usuarioMock = new UsuarioResponse();
        usuarioMock.setId(idUsuario);
        usuarioMock.setNombre("Estaban");
        usuarioMock.setAppaterno("Quinto");
        usuarioMock.setApmaterno("Gonzales");
        usuarioMock.setCorreo("e.quinto@gmail.com");
        usuarioMock.setDireccion("Calle falsa 123");
        usuarioMock.setTelefono("+569778996777");
        usuarioMock.setTipoUsuario("Cliente");

        ProductoResponse productoMock = new ProductoResponse();
        productoMock.setId(idProducto);
        productoMock.setNombrep("Peluche de Neco-Arc");
        productoMock.setDescripcion("Peluche oficial de 20cm, edición limitada Burunyuu.");
        productoMock.setPrecio(new BigDecimal("25.99"));

        // Definir comportamiento de los Clients
        when(usuarioClient.obtenerUsuario(idUsuario)).thenReturn(usuarioMock);
        when(productoClient.obtenerProducto(idProducto)).thenReturn(productoMock);

        // Instancias para la persistencia en BD local
        Carrito nuevoCarrito = new Carrito();
        nuevoCarrito.setIdUsuario(idUsuario);
        nuevoCarrito.setIdProducto(idProducto);
        nuevoCarrito.setCantidad(5);

        Carrito carritoGuardado = new Carrito();
        carritoGuardado.setId(100L);
        carritoGuardado.setCantidad(5);
        carritoGuardado.setMontoTotal(new BigDecimal("129.95"));

        CarritoResponse responseEsperada = new CarritoResponse();
        responseEsperada.setId(100L);
        responseEsperada.setCantidad(5);
        responseEsperada.setMontoTotal(new BigDecimal("129.95"));

        // definimos el Comportamiento de Repositorio y Mappers
        when(carritoRepository.findByIdUsuarioAndIdProducto(idUsuario, idProducto)).thenReturn(Optional.empty());
        when(carritoMapper.fromRequest(requestMock, idUsuario, idProducto)).thenReturn(nuevoCarrito);
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carritoGuardado);
        when(carritoMapper.toResponse(eq(carritoGuardado), any(UsuarioResponse.class), any(ProductoResponse.class)))
                .thenReturn(responseEsperada);

        //Ejecucion del metodo
        CarritoResponse resultado = carritoService.agregarProducto(requestMock, idUsuario, idProducto);

        //Hacemos las verificaciones
        assertNotNull(resultado);
        assertEquals(5, resultado.getCantidad());
        assertEquals(new BigDecimal("129.95"), resultado.getMontoTotal());
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    @Test
    void agregarProducto_CuandoYaExiste_DeberiaAcumularCantidadYMonto() {

        Long idUsuario = 5L;
        Long idProducto = 1L;

        // Mockear el Request con los nuevos datos de entrada que recibira el programa
        CarritoRequest requestMock = new CarritoRequest();
        requestMock.setCantidad(5);

        // Mockear las respuestas de las APIs externas
        UsuarioResponse usuarioMock = new UsuarioResponse();
        usuarioMock.setId(idUsuario);
        usuarioMock.setNombre("Estaban");
        usuarioMock.setAppaterno("Quinto");
        usuarioMock.setApmaterno("Gonzales");
        usuarioMock.setCorreo("e.quinto@gmail.com");
        usuarioMock.setDireccion("Calle falsa 123");
        usuarioMock.setTelefono("+569778996777");
        usuarioMock.setTipoUsuario("Cliente");

        ProductoResponse productoMock = new ProductoResponse();
        productoMock.setId(idProducto);
        productoMock.setNombrep("Peluche de Neco-Arc");
        productoMock.setDescripcion("Peluche oficial de 20cm, edición limitada Burunyuu.");
        productoMock.setPrecio(new BigDecimal("25.99"));

        when(usuarioClient.obtenerUsuario(idUsuario)).thenReturn(usuarioMock);
        when(productoClient.obtenerProducto(idProducto)).thenReturn(productoMock);

        //Simular el registro que ya existía en la base de datos antes de la llamada
        Carrito carritoExistente = new Carrito();
        carritoExistente.setId(5L);
        carritoExistente.setIdUsuario(idUsuario);
        carritoExistente.setIdProducto(idProducto);
        carritoExistente.setCantidad(6);
        carritoExistente.setMontoTotal(new BigDecimal("155.94"));

        // Lo que se espera que la logica haga es que realiza esta operacion: 6 + 5 = 11 unidades
        CarritoResponse responseEsperada = new CarritoResponse();
        responseEsperada.setId(5L);
        responseEsperada.setCantidad(11); 
        responseEsperada.setMontoTotal(new BigDecimal("285.89"));

        when(carritoRepository.findByIdUsuarioAndIdProducto(idUsuario, idProducto)).thenReturn(Optional.of(carritoExistente));
        when(carritoRepository.save(carritoExistente)).thenReturn(carritoExistente);
        when(carritoMapper.toResponse(carritoExistente, usuarioMock, productoMock)).thenReturn(responseEsperada);

        // se ejecuta el método
        CarritoResponse resultado = carritoService.agregarProducto(requestMock, idUsuario, idProducto);

        // y realizamos las verificaciones
        assertNotNull(resultado);
        assertEquals(11, resultado.getCantidad());
        assertEquals(new BigDecimal("285.89"), resultado.getMontoTotal());
        verify(carritoRepository, times(1)).save(carritoExistente);
    }

    @Test
    void obtenerCarritoPorUsuario_CuandoTieneProductos_DeberiaRetornarLista() {
    
        Long idUsuario = 5L;
        Long idProducto = 1L;

        // Mockear la respuesta del microservicio de usuario
        UsuarioResponse usuarioMock = new UsuarioResponse();
        usuarioMock.setId(idUsuario);
        usuarioMock.setNombre("Estaban");
        usuarioMock.setAppaterno("Quinto");
        usuarioMock.setApmaterno("Gonzales");
        usuarioMock.setCorreo("e.quinto@gmail.com");
        usuarioMock.setDireccion("Calle falsa 123");
        usuarioMock.setTelefono("+569778996777");
        usuarioMock.setTipoUsuario("Cliente");

        // mockeamos la respuesta del microservicio de productos 
        ProductoResponse productoMock = new ProductoResponse();
        productoMock.setId(idProducto);
        productoMock.setNombrep("Peluche de Neco-Arc");
        productoMock.setDescripcion("Peluche oficial de 20cm, edición limitada Burunyuu.");
        productoMock.setPrecio(new BigDecimal("25.99"));

        // definimos el comportamiento de los client
        when(usuarioClient.obtenerUsuario(idUsuario)).thenReturn(usuarioMock);
        when(productoClient.obtenerProducto(idProducto)).thenReturn(productoMock);

        // Simulamos lo que la base de datos local tiene guardado para este usuario
        Carrito itemCarrito = new Carrito();
        itemCarrito.setId(5L);
        itemCarrito.setIdUsuario(idUsuario);
        itemCarrito.setIdProducto(idProducto);
        itemCarrito.setCantidad(1);
        itemCarrito.setMontoTotal(new BigDecimal("25.99"));

        List<Carrito> listaCarritoBD = List.of(itemCarrito);

        // mockeamos la respuesta final que creara el mapper
        CarritoResponse responseMock = new CarritoResponse();
        responseMock.setId(100L);
        responseMock.setCantidad(1);
        responseMock.setMontoTotal(new BigDecimal("25.99"));

        // definimos el comportamiento del repositorio y mapper
        when(carritoRepository.findByIdUsuario(idUsuario)).thenReturn(listaCarritoBD);
        when(carritoMapper.toResponse(itemCarrito, usuarioMock, productoMock)).thenReturn(responseMock);

        // se ejecuta el metodo del servicio
        List<CarritoResponse> resultado = carritoService.obtenerCarritoPorUsuario(idUsuario);

        // y hacemos las comprobaciones
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size()); // Validamos que traiga un ítem
        assertEquals(1, resultado.get(0).getCantidad());
        assertEquals(new BigDecimal("25.99"), resultado.get(0).getMontoTotal());

        // para terminar de comprobar, verificamos que se haya consultado al repositorio una vez
        verify(carritoRepository, times(1)).findByIdUsuario(idUsuario);
    }

    @Test
    void agregarProducto_CuandoCantidadEsMenorOIgualACero_DeberiaLanzarIllegalArgumentException() {
        // --- 1. ARRANGE ---
        Long idUsuario = 5L;
        Long idProducto = 1L;

        // Intentamos agregar un producto con cantidad 0 (Invalido)
        CarritoRequest requestInvalido = new CarritoRequest();
        requestInvalido.setCantidad(0);

        // --- 2. ACT & 3. ASSERT ---
        // Usamos assertThrows para verificar que la lógica corte el flujo lanzando la excepción esperada
        IllegalArgumentException excepcion = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class, 
                () -> carritoService.agregarProducto(requestInvalido, idUsuario, idProducto)
        );

        // Opcional: Validamos que el mensaje sea exactamente el que programaste en tu Service
        assertEquals("La cantidad a agregar debe ser mayor a cero.", excepcion.getMessage());

        // Verificamos que NI SIQUIERA se llamaron a los repositorios ni a los mappers, ya que el flujo se corta al inicio
        verify(carritoRepository, times(0)).save(any(Carrito.class));
    }

    @Test
    void obtenerCarritoPorUsuario_CuandoCarritoEstaVacio_DeberiaLanzarNoSuchElementException() {
        
        Long idUsuario = 5L;

        // mockeamos los datos de usuario simulando que el microservicio de usuario responde bien
        UsuarioResponse usuarioMock = new UsuarioResponse();
        usuarioMock.setId(idUsuario);
        usuarioMock.setNombre("Estaban");

        when(usuarioClient.obtenerUsuario(idUsuario)).thenReturn(usuarioMock);

        // simulamos que el repositorio local retorna una lista vacía es decir sin productos en el carrito
        when(carritoRepository.findByIdUsuario(idUsuario)).thenReturn(List.of());

        // Se realiza el salto de la excepcion
        NoSuchElementException excepcion = org.junit.jupiter.api.Assertions.assertThrows(
                NoSuchElementException.class, 
                () -> carritoService.obtenerCarritoPorUsuario(idUsuario)
        );

        assertEquals("El carrito del usuario ID " + idUsuario + " no contiene productos.", excepcion.getMessage());

        // Por ultimo verificamos que nunca se llamó al productoClient ya que no habia elementos para mapear
        verify(productoClient, times(0)).obtenerProducto(anyLong());
    }

    @Test
    void actualizarCantidad_CuandoItemNoExiste_DeberiaLanzarNoSuchElementException() {
        
        Long idCarritoInvalido = 6L;
        Integer nuevaCantidad = 3;

        // simulamos que buscamos el item en la base de datos por id y retorna vacío con Optional.empty()
        when(carritoRepository.findById(idCarritoInvalido)).thenReturn(Optional.empty());

        // se hace el lanzamiento de la excepcion mas una comprobacion
        NoSuchElementException excepcion = org.junit.jupiter.api.Assertions.assertThrows(
                NoSuchElementException.class, 
                () -> carritoService.actualizarCantidad(idCarritoInvalido, nuevaCantidad)
        );

        assertEquals("No se encontró el ítem solicitado en el carrito.", excepcion.getMessage());

        // por ultimo nos aseguramos de que no intente guardar nada en la BD
        verify(carritoRepository, times(0)).save(any(Carrito.class));
    }
}