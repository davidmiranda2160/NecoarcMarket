package cl.duoc.carrito;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Agregar Producto - Cuando es nuevo deberia calcular monto y guardar")
    void agregarProducto_CuandoEsNuevo_DeberiaCalcularYGuardar() {

        Long idUsuario = 5L;
        Long idProducto = 1L;

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

        when(usuarioClient.obtenerUsuario(idUsuario)).thenReturn(usuarioMock);
        when(productoClient.obtenerProducto(idProducto)).thenReturn(productoMock);

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

        CarritoResponse resultado = carritoService.agregarProducto(requestMock, idUsuario, idProducto);

        assertNotNull(resultado);
        assertEquals(5, resultado.getCantidad());
        assertEquals(new BigDecimal("129.95"), resultado.getMontoTotal());
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    @Test
    @DisplayName("Agregar Producto - Cuando ya existe deberia acumular cantidad y monto")
    void agregarProducto_CuandoYaExiste_DeberiaAcumularCantidadYMonto() {

        Long idUsuario = 5L;
        Long idProducto = 1L;

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

        when(usuarioClient.obtenerUsuario(idUsuario)).thenReturn(usuarioMock);
        when(productoClient.obtenerProducto(idProducto)).thenReturn(productoMock);

        Carrito carritoExistente = new Carrito();
        carritoExistente.setId(5L);
        carritoExistente.setIdUsuario(idUsuario);
        carritoExistente.setIdProducto(idProducto);
        carritoExistente.setCantidad(6);
        carritoExistente.setMontoTotal(new BigDecimal("155.94"));

        CarritoResponse responseEsperada = new CarritoResponse();
        responseEsperada.setId(5L);
        responseEsperada.setCantidad(11); 
        responseEsperada.setMontoTotal(new BigDecimal("285.89"));

        when(carritoRepository.findByIdUsuarioAndIdProducto(idUsuario, idProducto)).thenReturn(Optional.of(carritoExistente));
        when(carritoRepository.save(carritoExistente)).thenReturn(carritoExistente);
        when(carritoMapper.toResponse(carritoExistente, usuarioMock, productoMock)).thenReturn(responseEsperada);

        CarritoResponse resultado = carritoService.agregarProducto(requestMock, idUsuario, idProducto);

        assertNotNull(resultado);
        assertEquals(11, resultado.getCantidad());
        assertEquals(new BigDecimal("285.89"), resultado.getMontoTotal());
        verify(carritoRepository, times(1)).save(carritoExistente);
    }

    @Test
    @DisplayName("Obtener Carrito - Cuando tiene productos deberia retornar la lista")
    void obtenerCarritoPorUsuario_CuandoTieneProductos_DeberiaRetornarLista() {
    
        Long idUsuario = 5L;
        Long idProducto = 1L;

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

        Carrito itemCarrito = new Carrito();
        itemCarrito.setId(5L);
        itemCarrito.setIdUsuario(idUsuario);
        itemCarrito.setIdProducto(idProducto);
        itemCarrito.setCantidad(1);
        itemCarrito.setMontoTotal(new BigDecimal("25.99"));

        List<Carrito> listaCarritoBD = List.of(itemCarrito);

        CarritoResponse responseMock = new CarritoResponse();
        responseMock.setId(100L);
        responseMock.setCantidad(1);
        responseMock.setMontoTotal(new BigDecimal("25.99"));

        
        when(carritoRepository.findByIdUsuario(idUsuario)).thenReturn(listaCarritoBD);
        when(carritoMapper.toResponse(itemCarrito, usuarioMock, productoMock)).thenReturn(responseMock);

        List<CarritoResponse> resultado = carritoService.obtenerCarritoPorUsuario(idUsuario);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size()); // Validamos que traiga un ítem
        assertEquals(1, resultado.get(0).getCantidad());
        assertEquals(new BigDecimal("25.99"), resultado.get(0).getMontoTotal());

        verify(carritoRepository, times(1)).findByIdUsuario(idUsuario);
    }

    @Test
    void agregarProducto_CuandoCantidadEsMenorOIgualACero_DeberiaLanzarIllegalArgumentException() {

        Long idUsuario = 5L;
        Long idProducto = 1L;


        CarritoRequest requestInvalido = new CarritoRequest();
        requestInvalido.setCantidad(0);


        IllegalArgumentException excepcion = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class, 
                () -> carritoService.agregarProducto(requestInvalido, idUsuario, idProducto)
        );


        assertEquals("La cantidad a agregar debe ser mayor a cero.", excepcion.getMessage());

        verify(carritoRepository, times(0)).save(any(Carrito.class));
    }

    @Test
    @DisplayName("Agregar Producto - Cuando cantidad es cero o menor deberia lanzar IllegalArgumentException")
    void obtenerCarritoPorUsuario_CuandoCarritoEstaVacio_DeberiaLanzarNoSuchElementException() {
        
        Long idUsuario = 5L;

        UsuarioResponse usuarioMock = new UsuarioResponse();
        usuarioMock.setId(idUsuario);
        usuarioMock.setNombre("Estaban");

        when(usuarioClient.obtenerUsuario(idUsuario)).thenReturn(usuarioMock);

        when(carritoRepository.findByIdUsuario(idUsuario)).thenReturn(List.of());

        NoSuchElementException excepcion = org.junit.jupiter.api.Assertions.assertThrows(
                NoSuchElementException.class, 
                () -> carritoService.obtenerCarritoPorUsuario(idUsuario)
        );

        assertEquals("El carrito del usuario ID " + idUsuario + " no contiene productos.", excepcion.getMessage());

        verify(productoClient, times(0)).obtenerProducto(anyLong());
    }

    @Test
    @DisplayName("Obtener Carrito - Cuando esta vacio deberia lanzar NoSuchElementException")
    void actualizarCantidad_CuandoItemNoExiste_DeberiaLanzarNoSuchElementException() {
        
        Long idCarritoInvalido = 6L;
        Integer nuevaCantidad = 3;

        when(carritoRepository.findById(idCarritoInvalido)).thenReturn(Optional.empty());

        NoSuchElementException excepcion = org.junit.jupiter.api.Assertions.assertThrows(
                NoSuchElementException.class, 
                () -> carritoService.actualizarCantidad(idCarritoInvalido, nuevaCantidad)
        );

        assertEquals("No se encontró el ítem solicitado en el carrito.", excepcion.getMessage());

        verify(carritoRepository, times(0)).save(any(Carrito.class));
    }
}