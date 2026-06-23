package cl.duoc.productos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.productos.client.InventarioClient;
import cl.duoc.productos.dto.InventarioResponse;
import cl.duoc.productos.dto.ProductoRequest;
import cl.duoc.productos.dto.ProductoResponse;
import cl.duoc.productos.dto.ProductoUpdateRequest;
import cl.duoc.productos.exception.ConflictException;
import cl.duoc.productos.mapper.ProductoMapper;
import cl.duoc.productos.model.Producto;
import cl.duoc.productos.repository.ProductoRepository;
import cl.duoc.productos.service.ProductoService;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private ProductoService productoService;

    @Test
    @DisplayName("Debería crear un producto exitosamente e inicializar su inventario en 0")
    void debeCrearProductoExitosamente() {
        // ------------------------------------------------------------------------
        // GIVEN: Preparación del escenario ficticio
        // ------------------------------------------------------------------------
        ProductoRequest request = new ProductoRequest();
        request.setNombrep("Peluche de Neco-Arc");
        request.setPrecio(new BigDecimal("25.99"));

        Producto productoMapeado = new Producto();
        productoMapeado.setNombrep("Peluche de Neco-Arc");
        productoMapeado.setPrecio(new BigDecimal("25.99"));

        Producto productoGuardado = new Producto();
        productoGuardado.setId(1L);
        productoGuardado.setNombrep("Peluche de Neco-Arc");
        productoGuardado.setPrecio(new BigDecimal("25.99"));
        productoGuardado.setActivo(true);

        ProductoResponse responseSimulado = new ProductoResponse();
        responseSimulado.setId(1L);
        responseSimulado.setNombrep("Peluche de Neco-Arc");
        responseSimulado.setStock(0);

        // Definimos comportamientos de los mocks
        when(productoRepository.existsByNombrep(request.getNombrep())).thenReturn(false);
        when(productoMapper.fromRequest(request)).thenReturn(productoMapeado);
        when(productoRepository.save(productoMapeado)).thenReturn(productoGuardado);
        when(productoMapper.toResponse(productoGuardado, 0)).thenReturn(responseSimulado);

        // ------------------------------------------------------------------------
        // WHEN: Ejecución de la acción
        // ------------------------------------------------------------------------
        ProductoResponse resultado = productoService.crearProducto(request);

        // ------------------------------------------------------------------------
        // THEN: Verificaciones y Aserciones precisas
        // ------------------------------------------------------------------------
        assertNotNull(resultado, "La respuesta no debería ser nula");
        assertEquals(1L, resultado.getId());
        assertEquals("Peluche de Neco-Arc", resultado.getNombrep());
        assertEquals(0, resultado.getStock());

        // Verificamos que se llamó al repositorio e inventario una sola vez
        verify(productoRepository, times(1)).existsByNombrep(request.getNombrep());
        verify(productoRepository, times(1)).save(productoMapeado);
        verify(inventarioClient, times(1)).crearRegistroInventario(any());
    }

    @Test
    @DisplayName("Debería lanzar ConflictException cuando se intenta registrar un nombre que ya existe")
    void debeLanzarExcepcionCuandoNombreYaExiste() {
        // ------------------------------------------------------------------------
        // GIVEN
        // ------------------------------------------------------------------------
        ProductoRequest request = new ProductoRequest();
        request.setNombrep("Peluche de Neco-Arc");

        when(productoRepository.existsByNombrep(request.getNombrep())).thenReturn(true);

        // ------------------------------------------------------------------------
        // WHEN & THEN
        // ------------------------------------------------------------------------
        ConflictException excepcion = assertThrows(ConflictException.class, () -> {
            productoService.crearProducto(request);
        });

        assertEquals("El nombre del producto ya existe", excepcion.getMessage());
        
        // Verificamos que jamás pasó por la etapa de guardado ni mapeo
        verify(productoRepository, never()).save(any(Producto.class));
        verify(inventarioClient, never()).crearRegistroInventario(any());
    }

    @Test
    @DisplayName("Debería retornar el producto con stock 0 si el microservicio de inventario está caído")
    void debeRetornarProductoConStockCeroSiInventarioFalla() {
        // ------------------------------------------------------------------------
        // GIVEN
        // ------------------------------------------------------------------------
        Long idBuscado = 1L;
        Producto productoMock = new Producto();
        productoMock.setId(idBuscado);
        productoMock.setNombrep("Peluche de Neco-Arc");

        ProductoResponse responseMock = new ProductoResponse();
        responseMock.setId(idBuscado);
        responseMock.setNombrep("Peluche de Neco-Arc");
        responseMock.setStock(0); // Forzado por el fallback del catch

        when(productoRepository.findById(idBuscado)).thenReturn(Optional.of(productoMock));
        
        // Simulamos un error de conexión lanzando una excepción al llamar al micro de inventario
        when(inventarioClient.obtenerStockPorProducto(idBuscado))
            .thenThrow(new RuntimeException("Conexión rechazada por el servidor"));

        when(productoMapper.toResponse(productoMock, 0)).thenReturn(responseMock);

        // ------------------------------------------------------------------------
        // WHEN
        // ------------------------------------------------------------------------
        ProductoResponse resultado = productoService.buscarProductoPorId(idBuscado);

        // ------------------------------------------------------------------------
        // THEN
        // ------------------------------------------------------------------------
        assertNotNull(resultado);
        assertEquals(0, resultado.getStock(), "El stock debería ser 0 debido a la caída controlada del servicio");
        verify(productoRepository, times(1)).findById(idBuscado);
        verify(productoMapper, times(1)).toResponse(productoMock, 0);
    }

    @Test
    @DisplayName("Debería lanzar NoSuchElementException cuando se busca un producto inexistente")
    void debeLanzarExcepcionCuandoProductoNoExiste() {
        // ------------------------------------------------------------------------
        // GIVEN
        // ------------------------------------------------------------------------
        Long idInexistente = 99L;
        when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ------------------------------------------------------------------------
        // WHEN & THEN
        // ------------------------------------------------------------------------
        NoSuchElementException excepcion = assertThrows(NoSuchElementException.class, () -> {
            productoService.buscarProductoPorId(idInexistente);
        });

        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(inventarioClient, never()).obtenerStockPorProducto(anyLong());
    }

    @Test
    @DisplayName("Debería retornar la lista completa de productos mapeados con sus stocks correspondientes")
    void debeBuscarProductosExitosamente() {
        // ------------------------------------------------------------------------
        // GIVEN
        // ------------------------------------------------------------------------
        Producto p1 = Producto.builder().id(1L).nombrep("Peluche Neco-Arc").precio(new BigDecimal("10")).build();
        Producto p2 = Producto.builder().id(2L).nombrep("Polera Neco-Arc").precio(new BigDecimal("15")).build();
        
        ProductoResponse r1 = ProductoResponse.builder().id(1L).nombrep("Peluche Neco-Arc").stock(5).build();
        ProductoResponse r2 = ProductoResponse.builder().id(2L).nombrep("Polera Neco-Arc").stock(12).build();

        when(productoRepository.findAll()).thenReturn(List.of(p1, p2));
        when(inventarioClient.obtenerStockPorProducto(1L)).thenReturn(InventarioResponse.builder().cantidad(5).build());
        when(inventarioClient.obtenerStockPorProducto(2L)).thenReturn(InventarioResponse.builder().cantidad(12).build());
        
        when(productoMapper.toResponse(p1, 5)).thenReturn(r1);
        when(productoMapper.toResponse(p2, 12)).thenReturn(r2);

        // ------------------------------------------------------------------------
        // WHEN
        // ------------------------------------------------------------------------
        List<ProductoResponse> resultado = productoService.buscarProductos();

        // ------------------------------------------------------------------------
        // THEN
        // ------------------------------------------------------------------------
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(5, resultado.get(0).getStock());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería actualizar los campos del producto de forma dinámica y retornar el DTO modificado")
    void debeActualizarProductoExitosamente() {
        // ------------------------------------------------------------------------
        // GIVEN
        // ------------------------------------------------------------------------
        Long idProd = 1L;
        ProductoUpdateRequest updateReq = new ProductoUpdateRequest();
        updateReq.setNombrep("Peluche Nuevo");
        updateReq.setPrecio(new BigDecimal("30.00"));

        Producto productoExistente = Producto.builder().id(idProd).nombrep("Peluche Viejo").precio(new BigDecimal("20")).build();
        Producto productoModificado = Producto.builder().id(idProd).nombrep("Peluche Nuevo").precio(new BigDecimal("30.00")).build();
        ProductoResponse responseMock = ProductoResponse.builder().id(idProd).nombrep("Peluche Nuevo").stock(10).build();

        when(productoRepository.findById(idProd)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.existsByNombrepAndIdNot("Peluche Nuevo", idProd)).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoModificado);
        when(inventarioClient.obtenerStockPorProducto(idProd)).thenReturn(InventarioResponse.builder().cantidad(10).build());
        when(productoMapper.toResponse(productoModificado, 10)).thenReturn(responseMock);

        // ------------------------------------------------------------------------
        // WHEN
        // ------------------------------------------------------------------------
        ProductoResponse resultado = productoService.actualizarProducto(idProd, updateReq);

        // ------------------------------------------------------------------------
        // THEN
        // ------------------------------------------------------------------------
        assertNotNull(resultado);
        assertEquals("Peluche Nuevo", resultado.getNombrep());
        assertEquals(10, resultado.getStock());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería eliminar el producto correctamente cuando el ID existe")
    void debeEliminarProductoExitosamente() {
        // ------------------------------------------------------------------------
        // GIVEN
        // ------------------------------------------------------------------------
        Long idEliminar = 1L;
        when(productoRepository.existsById(idEliminar)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(idEliminar);

        // ------------------------------------------------------------------------
        // WHEN
        // ------------------------------------------------------------------------
        productoService.eliminarProducto(idEliminar);

        // ------------------------------------------------------------------------
        // THEN
        // ------------------------------------------------------------------------
        verify(productoRepository, times(1)).existsById(idEliminar);
        verify(productoRepository, times(1)).deleteById(idEliminar);
    }
}
