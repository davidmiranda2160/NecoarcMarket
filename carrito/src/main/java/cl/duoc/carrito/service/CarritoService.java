package cl.duoc.carrito.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.carrito.dto.CarritoRequest;
import cl.duoc.carrito.dto.CarritoResponse;
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

    public CarritoResponse agregarProducto(CarritoRequest request) {
        try {
            Optional<Carrito> carritoExistente = carritoRepository
                    .findByProductobyUsuario(request.getIdUsuario(), request.getIdProducto());

            Carrito productoAgregado;

            if (carritoExistente.isPresent()) {
                Carrito carrito = carritoExistente.get();
                carrito.setCantidad(carrito.getCantidad() + request.getCantidad());
                carrito.setMontoTotal(carrito.getMontoTotal() + request.getMontoTotal());
                productoAgregado = carritoRepository.save(carrito);
                log.info("Cantidad actualizada en el carrito");
            } else {
                Carrito nuevoCarrito = carritoMapper.fromRequest(request);
                productoAgregado = carritoRepository.save(nuevoCarrito);
                log.info("Nuevo producto agregado al carrito");
            }
            return carritoMapper.toResponse(productoAgregado);
        } catch (Exception e) {
            log.error("Error al agregar producto al carrito: ", e.getMessage());
            return null;
        }
    }

    public List<CarritoResponse> obtenerCarritoPorUsuario(Long idUsuario) {
        List<Carrito> items = carritoRepository.findByIdUsuario(idUsuario);
        if (items.isEmpty()) {
            log.warn("El carrito del usuario está vacío");
        }

        return items.stream()
                .map(carritoMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CarritoResponse actualizarCantidad(Long id, int nuevaCantidad, int nuevoMonto) {
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el ítem en el carrito"));

        carrito.setCantidad(nuevaCantidad);
        carrito.setMontoTotal(nuevoMonto);

        Carrito actualizado = carritoRepository.save(carrito);
        log.info("Ítem del carrito ID actualizado", id);
        return carritoMapper.toResponse(actualizado);
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