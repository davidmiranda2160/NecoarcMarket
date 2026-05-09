package cl.duoc.productos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.productos.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    //Buscar producto por nombre
    Optional<Producto> findByNombrep(String nombrep);

    //verificar existencia de producto por nombre
    boolean existsByNombrep(String nombrep);

    //Verifica el producto por el nombre usando un id específico
    boolean existsByNombrepAndIdNot(String nombrep, Long id);



}
