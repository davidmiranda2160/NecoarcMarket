package cl.duoc.inventario;

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

import cl.duoc.inventario.client.ProductoClient;
import cl.duoc.inventario.dto.InventarioResponse;
import cl.duoc.inventario.dto.ProductoDetalleDTO;
import cl.duoc.inventario.dto.ProductoResponse;
import cl.duoc.inventario.exception.ConflictException;
import cl.duoc.inventario.mapper.InventarioMapper;
import cl.duoc.inventario.model.Inventario;
import cl.duoc.inventario.repository.InventarioRepository;
import cl.duoc.inventario.service.InventarioService;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private InventarioMapper inventarioMapper;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private InventarioService inventarioService;



    @Test
    @DisplayName("Debería retornar el stock de un producto si tiene registro en la base de datos")
    void debeObtenerStockPorProductoExitosamente() {
        // GIVEN
        Long prodId = 1L;
        Inventario invMock = Inventario.builder().id(10L).productoId(prodId).cantidad(15).build();
        InventarioResponse resMock = InventarioResponse.builder().productoId(prodId).cantidad(15).build();

        when(inventarioRepository.findByProductoId(prodId)).thenReturn(Optional.of(invMock));
        when(inventarioMapper.toResponse(invMock)).thenReturn(resMock);

        // WHEN
        InventarioResponse resultado = inventarioService.obtenerStockPorProducto(prodId);

        // THEN
        assertNotNull(resultado);
        assertEquals(15, resultado.getCantidad());
        verify(inventarioRepository, times(1)).findByProductoId(prodId);
    }

    @Test
    @DisplayName("Debería lanzar NoSuchElementException cuando el producto no existe en inventario")
    void debeLanzarExcepcionCuandoNoExisteStockPorProducto() {
        // GIVEN
        Long prodId = 99L;
        when(inventarioRepository.findByProductoId(prodId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(NoSuchElementException.class, () -> {
            inventarioService.obtenerStockPorProducto(prodId);
        });
    }



    @Test
    @DisplayName("Debería validar la existencia en la API externa y acumular el nuevo stock")
    void debeAgregarStockExitosamente() {
        // GIVEN
        Long prodId = 1L;
        Inventario invExistente = Inventario.builder().productoId(prodId).cantidad(10).build();
        InventarioResponse resMock = InventarioResponse.builder().productoId(prodId).cantidad(25).build();

        when(productoClient.obtenerProductoPorId(prodId)).thenReturn(new ProductoResponse());
        when(inventarioRepository.findByProductoId(prodId)).thenReturn(Optional.of(invExistente));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(inventarioMapper.toResponse(any(Inventario.class))).thenReturn(resMock);

        // WHEN
        InventarioResponse resultado = inventarioService.agregarStock(prodId, 15);

        // THEN
        assertNotNull(resultado);
        assertEquals(25, resultado.getCantidad());
        verify(productoClient, times(1)).obtenerProductoPorId(prodId);
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }



    @Test
    @DisplayName("Debería restar el stock correctamente cuando hay unidades suficientes")
    void debeDescontarStockExitosamente() {
        // GIVEN
        Long prodId = 1L;
        Inventario invExistente = Inventario.builder().productoId(prodId).cantidad(50).build();
        InventarioResponse resMock = InventarioResponse.builder().productoId(prodId).cantidad(20).build();

        when(inventarioRepository.findByProductoId(prodId)).thenReturn(Optional.of(invExistente));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(inventarioMapper.toResponse(any(Inventario.class))).thenReturn(resMock);

        // WHEN
        InventarioResponse resultado = inventarioService.descontarStock(prodId, 30);

        // THEN
        assertNotNull(resultado);
        assertEquals(20, resultado.getCantidad());
    }

    @Test
    @DisplayName("Debería lanzar ConflictException si la cantidad a descontar supera al stock disponible")
    void debeLanzarConflictExceptionCuandoStockInsuficiente() {
        // GIVEN
        Long prodId = 1L;
        Inventario invExistente = Inventario.builder().productoId(prodId).cantidad(5).build();

        when(inventarioRepository.findByProductoId(prodId)).thenReturn(Optional.of(invExistente));

        // WHEN & THEN
        ConflictException ex = assertThrows(ConflictException.class, () -> {
            inventarioService.descontarStock(prodId, 10);
        });

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }



    @Test
    @DisplayName("Debería retornar verdadero si el stock actual cubre o iguala la cantidad requerida")
    void debeValidarTieneStockSuficiente() {
        // GIVEN
        Long prodId = 1L;
        Inventario invMock = Inventario.builder().productoId(prodId).cantidad(100).build();
        when(inventarioRepository.findByProductoId(prodId)).thenReturn(Optional.of(invMock));

        // WHEN
        boolean conSuficiente = inventarioService.tieneStockSuficiente(prodId, 50);
        boolean conExcedido = inventarioService.tieneStockSuficiente(prodId, 150);

        // THEN
        assertTrue(conSuficiente);
        assertFalse(conExcedido);
    }


    @Test
    @DisplayName("Debería cruzar los datos locales con la API externa para armar el DTO de detalle completo")
    void debeObtenerDetalleCompletoExitosamente() {
        // GIVEN
        Long prodId = 1L;
        Inventario invMock = Inventario.builder().productoId(prodId).cantidad(40).build();
        ProductoResponse prodMock = new ProductoResponse();
        ProductoDetalleDTO detalleMock = new ProductoDetalleDTO();

        when(inventarioRepository.findByProductoId(prodId)).thenReturn(Optional.of(invMock));
        when(productoClient.obtenerProductoPorId(prodId)).thenReturn(prodMock);
        when(inventarioMapper.toDetalleDTO(invMock, prodMock)).thenReturn(detalleMock);

        // WHEN
        ProductoDetalleDTO resultado = inventarioService.obtenerDetalleCompleto(prodId);

        // THEN
        assertNotNull(resultado);
        verify(productoClient, times(1)).obtenerProductoPorId(prodId);
    }


    @Test
    @DisplayName("Debería eliminar físicamente el registro de la base de datos si el ID existe")
    void debeEliminarInventarioPorProductoExitosamente() {
        // GIVEN
        Long prodId = 1L;
        Inventario invMock = Inventario.builder().productoId(prodId).build();

        when(inventarioRepository.findByProductoId(prodId)).thenReturn(Optional.of(invMock));
        doNothing().when(inventarioRepository).delete(invMock);

        // WHEN
        inventarioService.eliminarInventarioPorProducto(prodId);

        // THEN
        verify(inventarioRepository, times(1)).delete(invMock);
    }
}
