package cl.duoc.ordenes;

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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.ordenes.client.CarritoClient;
import cl.duoc.ordenes.client.InventarioClient;
import cl.duoc.ordenes.dto.CarritoResponse;
import cl.duoc.ordenes.dto.OrdenesDetalleResponse;
import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.dto.ProductoResponse;
import cl.duoc.ordenes.mapper.OrdenesMapper;
import cl.duoc.ordenes.model.Ordenes;
import cl.duoc.ordenes.model.OrdenesDetalle;
import cl.duoc.ordenes.repository.OrdenesDetalleRepository;
import cl.duoc.ordenes.repository.OrdenesRepository;
import cl.duoc.ordenes.service.OrdenesService;

@ExtendWith(MockitoExtension.class)
public class OrdenesServiceTest {

    @Mock private OrdenesRepository ordenesRepository;
    @Mock private OrdenesDetalleRepository ordenesDetalleRepository;
    @Mock private OrdenesMapper ordenesMapper;
    @Mock private CarritoClient carritoClient;
    @Mock private InventarioClient inventarioClient;

    @InjectMocks private OrdenesService ordenesService;

    @Test
    @DisplayName("Debería crear una orden correctamente descontando stock del inventario y limpiando el carrito del usuario")
    void cuandoCrearOrdenEsExitoso_entoncesDescuentaStockYVaciaCarrito() {
        OrdenesRequest request = new OrdenesRequest();
        request.setUsuarioId(5L);

        CarritoResponse carritoItem = new CarritoResponse();
        carritoItem.setMontoTotal(new BigDecimal("207.92"));
        carritoItem.setCantidad(8);
        
        ProductoResponse producto = new ProductoResponse();
        producto.setId(1L);
        carritoItem.setProducto(producto);

        Ordenes orden = new Ordenes();
        orden.setId(5L);
        orden.setUsuarioId(5L);
        orden.setEstadoOrden("Pendiente");
        orden.setTotal(new BigDecimal("207.92"));

        OrdenesDetalle detalle = new OrdenesDetalle();
        detalle.setId(10L);
        detalle.setOrdenId(5L);
        detalle.setIdProducto(1L);
        detalle.setCantidad(8);

        OrdenesResponse responseEsperada = new OrdenesResponse();
        responseEsperada.setId(5L);
        responseEsperada.setUsuarioId(5L);
        responseEsperada.setTotal(new BigDecimal("207.92"));
        responseEsperada.setEstadoOrden("Pendiente");
        responseEsperada.setFechaOrden(java.time.LocalDateTime.now());
        responseEsperada.setItems(null);

        OrdenesDetalleResponse responseDetalle = new OrdenesDetalleResponse();
        responseDetalle.setIdProducto(1L);
        responseDetalle.setCantidad(8);
        responseDetalle.setPrecioUnitario(new BigDecimal("25.99"));
        responseEsperada.setDetalles(List.of(responseDetalle));

        when(carritoClient.obtenerCarritoPorUsuario(5L)).thenReturn(List.of(carritoItem));
        when(ordenesMapper.fromRequest(any(OrdenesRequest.class), any(BigDecimal.class))).thenReturn(orden);
        when(ordenesRepository.save(any(Ordenes.class))).thenReturn(orden);
        when(ordenesMapper.toDetalleEntity(any(CarritoResponse.class), eq(5L))).thenReturn(detalle);
        when(ordenesDetalleRepository.saveAll(anyList())).thenReturn(List.of(detalle));
        when(ordenesMapper.toResponse(any(Ordenes.class), anyList())).thenReturn(responseEsperada);

        OrdenesResponse resultado = ordenesService.crearOrden(request);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals(5L, resultado.getUsuarioId());
        assertEquals(new BigDecimal("207.92"), resultado.getTotal());
        assertEquals("Pendiente", resultado.getEstadoOrden());
        
        verify(inventarioClient, times(1)).descontarStock(1L, 8);
        verify(carritoClient, times(1)).limpiarCarrito(5L);
        verify(ordenesRepository, times(1)).save(orden);
    }

    @Test
    @DisplayName("Debería lanzar NoSuchElementException al intentar crear una orden si el carrito del usuario está vacío")
    void cuandoCrearOrdenConCarritoVacio_entoncesLanzaNoSuchElementException() {
        OrdenesRequest request = new OrdenesRequest();
        request.setUsuarioId(5L);
        
        when(carritoClient.obtenerCarritoPorUsuario(5L)).thenReturn(List.of());

        NoSuchElementException excepcion = assertThrows(NoSuchElementException.class, () -> {
            ordenesService.crearOrden(request);
        });

        assertEquals("No hay productos en el carrito para generar una orden.", excepcion.getMessage());
        verify(inventarioClient, never()).descontarStock(any(Long.class), any(Integer.class));
        verify(ordenesRepository, never()).save(any(Ordenes.class));
    }

    @Test
    @DisplayName("Debería retornar la orden correspondiente con sus líneas de detalle cuando el ID de orden existe")
    void cuandoObtenerPorIdExistente_entoncesDevuelveOrdenResponse() {
        Long id = 5L;
        Ordenes orden = new Ordenes();
        orden.setId(id);

        OrdenesDetalle detalle = new OrdenesDetalle();
        detalle.setIdProducto(1L);
        detalle.setCantidad(8);
        List<OrdenesDetalle> detalles = List.of(detalle);

        OrdenesResponse responseEsperada = new OrdenesResponse();
        responseEsperada.setId(5L);
        responseEsperada.setUsuarioId(5L);
        responseEsperada.setTotal(new BigDecimal("207.92"));
        responseEsperada.setEstadoOrden("Pendiente");
        responseEsperada.setFechaOrden(java.time.LocalDateTime.now());

        OrdenesDetalleResponse responseDetalle = new OrdenesDetalleResponse();
        responseDetalle.setIdProducto(1L);
        responseDetalle.setCantidad(8);
        responseDetalle.setPrecioUnitario(new BigDecimal("25.99"));
        responseEsperada.setDetalles(List.of(responseDetalle));

        when(ordenesRepository.findById(id)).thenReturn(Optional.of(orden));
        when(ordenesDetalleRepository.findByOrdenId(id)).thenReturn(detalles);
        when(ordenesMapper.toResponse(orden, detalles)).thenReturn(responseEsperada);

        OrdenesResponse resultado = ordenesService.obtenerPorId(id);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals(new BigDecimal("207.92"), resultado.getTotal());
        verify(ordenesRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debería lanzar NoSuchElementException al consultar una orden por un ID que no se encuentra registrado")
    void cuandoObtenerPorIdInexistente_entoncesLanzaNoSuchElementException() {
        Long idInexistente = 99L;
        when(ordenesRepository.findById(idInexistente)).thenReturn(Optional.empty());

        NoSuchElementException excepcion = assertThrows(NoSuchElementException.class, () -> {
            ordenesService.obtenerPorId(idInexistente);
        });

        assertEquals("Orden no encontrada con ID: " + idInexistente, excepcion.getMessage());
        verify(ordenesRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Debería retornar el catálogo completo de órdenes existentes en el sistema")
    void cuandoObtenerTodas_entoncesDevuelveListaDeOrdenes() {
        Ordenes orden1 = new Ordenes();
        orden1.setId(5L);

        OrdenesResponse responseEsperada = new OrdenesResponse();
        responseEsperada.setId(5L);
        responseEsperada.setUsuarioId(5L);
        responseEsperada.setTotal(new BigDecimal("207.92"));
        responseEsperada.setEstadoOrden("Pendiente");
        
        OrdenesDetalleResponse responseDetalle = new OrdenesDetalleResponse();
        responseDetalle.setIdProducto(1L);
        responseDetalle.setCantidad(8);
        responseDetalle.setPrecioUnitario(new BigDecimal("25.99"));
        responseEsperada.setDetalles(List.of(responseDetalle));

        when(ordenesRepository.findAll()).thenReturn(List.of(orden1));
        // Ajustado para simular la búsqueda de detalles real del ciclo del service
        when(ordenesDetalleRepository.findByOrdenId(5L)).thenReturn(List.of(new OrdenesDetalle()));
        when(ordenesMapper.toResponse(any(Ordenes.class), anyList())).thenReturn(responseEsperada);

        List<OrdenesResponse> resultado = ordenesService.obtenerTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(new BigDecimal("207.92"), resultado.get(0).getTotal());
        verify(ordenesRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería lanzar NoSuchElementException cuando se buscan las órdenes de un usuario que no tiene registros")
    void cuandoUsuarioNoTieneOrdenes_entoncesLanzaNoSuchElementException() {
        Long usuarioId = 5L;
        when(ordenesRepository.findByUsuarioId(usuarioId)).thenReturn(List.of());

        NoSuchElementException excepcion = assertThrows(NoSuchElementException.class, () -> {
            ordenesService.obtenerPorUsuario(usuarioId);
        });

        assertEquals("No se encontraron ordenes para el usuario con id: " + usuarioId, excepcion.getMessage());
        verify(ordenesRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Debería lanzar NoSuchElementException cuando no existen órdenes con el estado indicado en el sistema")
    void cuandoNoHayOrdenesConEseEstado_entoncesLanzaNoSuchElementException() {
        String estadoInvalido = "INVALIDO";
        when(ordenesRepository.findByEstadoOrden(estadoInvalido)).thenReturn(List.of());

        NoSuchElementException excepcion = assertThrows(NoSuchElementException.class, () -> {
            ordenesService.obtenerPorEstado(estadoInvalido);
        });

        assertEquals("No se encontraron órdenes con el estado: " + estadoInvalido, excepcion.getMessage());
        verify(ordenesRepository, times(1)).findByEstadoOrden(estadoInvalido);
    }

    @Test
    @DisplayName("Debería reintegrar el stock al inventario de manera correcta cuando el nuevo estado de la orden es Cancelada")
    void cuandoActualizarEstadoACancelada_entoncesReintegraStockEnInventario() {
        Long idOrden = 5L;
        Ordenes orden = new Ordenes();
        orden.setId(idOrden);
        orden.setEstadoOrden("Pendiente");

        OrdenesDetalle detalle = new OrdenesDetalle();
        detalle.setIdProducto(1L);
        detalle.setCantidad(8);

        when(ordenesRepository.findById(idOrden)).thenReturn(Optional.of(orden));
        when(ordenesDetalleRepository.findByOrdenId(idOrden)).thenReturn(List.of(detalle));
        when(ordenesRepository.save(any(Ordenes.class))).thenReturn(orden);

        ordenesService.actualizarEstadoDesdePagos(idOrden, "Cancelada");

        assertEquals("Cancelada", orden.getEstadoOrden());
        verify(inventarioClient, times(1)).reintegrarStock(1L, 8);
        verify(ordenesRepository, times(1)).save(orden);
    }
}