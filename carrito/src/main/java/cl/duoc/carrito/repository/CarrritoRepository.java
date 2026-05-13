package cl.duoc.carrito.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.carrito.model.Carrito;

@Repository
public interface CarrritoRepository extends JpaRepository<Carrito, Long>{
    //Para obtener todos los productos que un usuario tiene en su carrito
    List<Carrito> findByIdUsuario(Long idUsuario);

    //Para verificar si un usuario ya tiene UN producto específico en su carrito
    Optional<Carrito> findByIdUsuarioAndIdProducto(Long idUsuario, Long idProducto);

    //Para borrar todo el carrito de un usuario de una sola vez
    void deleteByIdUsuario(Long idUsuario);

}
