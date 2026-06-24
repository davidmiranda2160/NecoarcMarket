package cl.duoc.ordenes;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.ordenes.client.CarritoClient;
import cl.duoc.ordenes.client.InventarioClient;
import cl.duoc.ordenes.dto.CarritoResponse;
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
    @Mock 
    private OrdenesRepository ordenesRepository;

    @Mock 
    private OrdenesDetalleRepository ordenesDetalleRepository;

    @Mock 
    private OrdenesMapper ordenesMapper;

    @Mock 
    private CarritoClient carritoClient;

    @Mock private InventarioClient inventarioClient;

    @InjectMocks private OrdenesService ordenesService;

   @Test
void cuandoCrearOrdenEsExitoso_entoncesDescuentaStockYVaciaCarrito() {
    // 1. GIVEN (Preparación de datos localmente con tus datos reales)
    OrdenesRequest request = new OrdenesRequest();
    request.setUsuarioId(5L); // usuarioId: 5

    CarritoResponse carritoItem = new CarritoResponse();
    carritoItem.setMontoTotal(new BigDecimal("207.92")); // total: 207.92
    carritoItem.setCantidad(8); // cantidad: 8
    
   ProductoResponse producto = new ProductoResponse();
    producto.setId(1L); // Asegúrate de que ProductoResponse tenga el método setId(Long id)
    carritoItem.setProducto(producto);

    Ordenes orden = new Ordenes();
    orden.setId(5L); // id: 5
    orden.setUsuarioId(5L);
    orden.setEstadoOrden("Pendiente");
    orden.setTotal(new BigDecimal("207.92"));

    OrdenesDetalle detalle = new OrdenesDetalle();
    detalle.setId(10L);
    detalle.setOrdenId(5L);
    detalle.setIdProducto(1L);
    detalle.setCantidad(8); // cantidad: 8

    OrdenesResponse responseEsperada = new OrdenesResponse();
    responseEsperada.setId(5L);

    // Configuración de los Mocks para que reaccionen a estos datos
    when(carritoClient.obtenerCarritoPorUsuario(5L)).thenReturn(List.of(carritoItem));
    when(ordenesMapper.fromRequest(any(OrdenesRequest.class), any(BigDecimal.class))).thenReturn(orden);
    when(ordenesRepository.save(any(Ordenes.class))).thenReturn(orden);
    when(ordenesMapper.toDetalleEntity(any(CarritoResponse.class), eq(5L))).thenReturn(detalle);
    when(ordenesDetalleRepository.saveAll(anyList())).thenReturn(List.of(detalle));
    when(ordenesMapper.toResponse(any(Ordenes.class), anyList())).thenReturn(responseEsperada);

    // 2. WHEN (Ejecución de la acción del servicio)
    OrdenesResponse resultado = ordenesService.crearOrden(request);

    // 3. THEN (Verificaciones de salida y comportamiento)
    assertNotNull(resultado);
    assertEquals(5L, resultado.getId());
    
    // Verifica que se descuenten exactamente 8 unidades del producto 1 en el inventario
    verify(inventarioClient, times(1)).descontarStock(1L, 8);
    // Verifica que se limpie el carrito del usuario 5
    verify(carritoClient, times(1)).limpiarCarrito(5L);
    verify(ordenesRepository, times(1)).save(orden);
}
}
