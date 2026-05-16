package cl.duoc.carrito.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.carrito.client.ProductoClient;
import cl.duoc.carrito.client.UsuarioClient;
import cl.duoc.carrito.dto.CarritoRequest;
import cl.duoc.carrito.dto.CarritoResponse;
import cl.duoc.carrito.dto.ProductoResponse;
import cl.duoc.carrito.dto.UsuarioResponse;
import cl.duoc.carrito.mapper.CarritoMapper;
import cl.duoc.carrito.model.Carrito;
import cl.duoc.carrito.repository.CarrritoRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CarritoService {

    @Autowired
    private CarrritoRepository carritoRepository;

    @Autowired
    private CarritoMapper carritoMapper;

    @Autowired
    private ProductoClient productoClient;

    @Autowired
    private UsuarioClient usuarioClient;

    public CarritoResponse agregarProducto(CarritoRequest request, Long idUsuario, Long idProducto) {
        try {

            Optional<Carrito> carritoExistente = carritoRepository
                    .findByIdUsuarioAndIdProducto(idUsuario, idProducto);

            Carrito productoAgregado;

            if (carritoExistente.isPresent()) {
                Carrito carrito = carritoExistente.get();
                carrito.setCantidad(carrito.getCantidad() + request.getCantidad());
                carrito.setMontoTotal(carrito.getMontoTotal().add(request.getMontoTotal()));
                productoAgregado = carritoRepository.save(carrito);
                log.info("Cantidad actualizada en el carrito");
            } else {
                Carrito nuevoCarrito = carritoMapper.fromRequest(request, idUsuario, idProducto);
                productoAgregado = carritoRepository.save(nuevoCarrito);
                log.info("Nuevo producto agregado al carrito del usuario");
            }

            UsuarioResponse user = usuarioClient.obtenerUsuario(idUsuario);
            ProductoResponse prod = productoClient.obtenerProducto(idProducto);

            return carritoMapper.toResponse(productoAgregado, user, prod);

        } catch (Exception e) {
            log.error("Error al agregar producto al carrito: ", e.getMessage());
            return null;
        }
    }

    public List<CarritoResponse> obtenerCarritoPorUsuario(Long idUsuario) {
        List<Carrito> items = carritoRepository.findByIdUsuario(idUsuario);

        UsuarioResponse userDto = usuarioClient.obtenerUsuario(idUsuario);

        return items.stream()
                .map(item -> {
                    ProductoResponse prodDto = productoClient.obtenerProducto(item.getIdProducto());
                    return carritoMapper.toResponse(item, userDto, prodDto);
                })
                .collect(Collectors.toList());
    }

    public CarritoResponse actualizarCantidad(Long id, int nuevaCantidad, BigDecimal nuevoMonto) {
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el ítem en el carrito"));

        carrito.setCantidad(nuevaCantidad);
        carrito.setMontoTotal(nuevoMonto);
        Carrito actualizado = carritoRepository.save(carrito);

        UsuarioResponse userDto = usuarioClient.obtenerUsuario(actualizado.getIdUsuario());
        ProductoResponse prodDto = productoClient.obtenerProducto(actualizado.getIdProducto());

        log.info("Ítem del carrito ID {} actualizado", id);
        return carritoMapper.toResponse(actualizado, userDto, prodDto);
    }

    public void eliminarProductoPorId(Long id) {
        if (!carritoRepository.existsById(id)) {
            log.error("No se encontró el producto con id: ", id);
            throw new NoSuchElementException("producto no encontrado");
        }
        carritoRepository.deleteById(id);
        log.info("Producto eliminado del carrito");
    }

    public void vaciarCarrito(Long idUsuario) {
        carritoRepository.deleteByIdUsuario(idUsuario);
        log.info("Carrito del usuario vaciado correctamente", idUsuario);
    }
}
