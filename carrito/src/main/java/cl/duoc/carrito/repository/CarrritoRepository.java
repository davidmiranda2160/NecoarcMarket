package cl.duoc.carrito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.carrito.model.Carrito;

@Repository
public interface CarrritoRepository extends JpaRepository<Carrito, Long>{

}
