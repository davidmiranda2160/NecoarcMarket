package cl.duoc.carrito.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
Este capa se encarga de gestionar la logica del negocio del carrito de compras
es quien se encarga de las operaciones CRUD en la base de datos local y la forma 
y la comunicacion con los microservicios de Usuario y Productos, todo es 
transaccional lo que garantiza consistencia en los datos
 */

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CarritoService {

    private final CarrritoRepository carritoRepository;

    private final CarritoMapper carritoMapper;

    private final ProductoClient productoClient;

    private final UsuarioClient usuarioClient;

    /*
    Agrega un producto al carrito de un usuario, la logica es que si el producto ya existe
    en el carrito del usuario esre se incremente y acomule un monto total
    */
    public CarritoResponse agregarProducto(CarritoRequest request, Long idUsuario, Long idProducto) {
        log.info("Procesando adición de producto ID {} para el usuario ID {}", idProducto, idUsuario);

        if (request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad a agregar debe ser mayor a cero.");
        }

        UsuarioResponse user = usuarioClient.obtenerUsuario(idUsuario);
        ProductoResponse prod = productoClient.obtenerProducto(idProducto);

        java.math.BigDecimal precioProducto = prod.getPrecio();
        java.math.BigDecimal montoDeEstaAdicion = precioProducto.multiply(java.math.BigDecimal.valueOf(request.getCantidad()));

        Optional<Carrito> carritoExistente = carritoRepository
                .findByIdUsuarioAndIdProducto(idUsuario, idProducto);

        Carrito productoAgregado;

        if (carritoExistente.isPresent()) {
            Carrito carrito = carritoExistente.get();
            carrito.setCantidad(carrito.getCantidad() + request.getCantidad());
            carrito.setMontoTotal(carrito.getMontoTotal().add(montoDeEstaAdicion));
            productoAgregado = carritoRepository.save(carrito);
            log.info("Cantidad modificada en el registro de carrito existente");
        } else {
            //Primero el mapper inicializa el objeto desde el request (montoTotal queda null)
            Carrito nuevoCarrito = carritoMapper.fromRequest(request, idUsuario, idProducto);

            //Luego le asignamos el monto calculado al objeto en memoria primero
            nuevoCarrito.setMontoTotal(montoDeEstaAdicion);

            //Y finalmente ahora si guardamos de forma segura en la base de datos sin nulos que generen error
            productoAgregado = carritoRepository.save(nuevoCarrito);
            log.info("Nuevo registro creado en el carrito");
        }

        return carritoMapper.toResponse(productoAgregado, user, prod);
    }

    /*
    Este metodo es el que se encarga de recuperar los productos que un usuario
    tiene en su carrito
     */
    public List<CarritoResponse> obtenerCarritoPorUsuario(Long idUsuario) {
        log.info("Buscando carrito del usuario ID: {}", idUsuario);

        UsuarioResponse userDto = usuarioClient.obtenerUsuario(idUsuario);

        List<Carrito> items = carritoRepository.findByIdUsuario(idUsuario);

        if (items.isEmpty()) {
            throw new NoSuchElementException("El carrito del usuario ID " + idUsuario + " no contiene productos.");
        }

        log.info("Se encontraron {} artículos en el carrito. Cargando detalles de productos...", items.size());

        return items.stream()
                .map(item -> {
                    ProductoResponse prodDto = productoClient.obtenerProducto(item.getIdProducto());
                    return carritoMapper.toResponse(item, userDto, prodDto);
                })
                .collect(Collectors.toList());
    }

    /*
    Este metodo actualiza de forma directa la cantidad y el monto acomulado de un item que se encuentre 
    en el carrito
     */
    public CarritoResponse actualizarCantidad(Long id, Integer nuevaCantidad) {
        log.info("Actualizando unidades del ítem de carrito ID: {}", id);

        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La nueva cantidad debe ser superior a cero.");
        }

        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el ítem solicitado en el carrito."));

        ProductoResponse prodDto = productoClient.obtenerProducto(carrito.getIdProducto());

        java.math.BigDecimal precioProducto = prodDto.getPrecio();
        java.math.BigDecimal montoCalculado = precioProducto.multiply(java.math.BigDecimal.valueOf(nuevaCantidad));

        carrito.setCantidad(nuevaCantidad);
        carrito.setMontoTotal(montoCalculado);

        Carrito actualizado = carritoRepository.save(carrito);

        UsuarioResponse userDto = usuarioClient.obtenerUsuario(actualizado.getIdUsuario());

        return carritoMapper.toResponse(actualizado, userDto, prodDto);
    }

    public void eliminarProductoPorId(Long id) {
        log.info("Eliminando ítem de carrito ID: {}", id);

        if (!carritoRepository.existsById(id)) {
            log.error("Error al intentar eliminar: no existe el registro con ID: {}", id);
            throw new NoSuchElementException("No se pudo eliminar el producto porque no existe en el carrito.");
        }

        carritoRepository.deleteById(id);
        log.info("Ítem eliminado con éxito");
    }

    public void vaciarCarrito(Long idUsuario) {
        log.info("Vaciando por completo el carrito del usuario ID: {}", idUsuario);

        usuarioClient.obtenerUsuario(idUsuario);

        carritoRepository.deleteByIdUsuario(idUsuario);
        log.info("Todos los productos del usuario han sido removidos");
    }
}
