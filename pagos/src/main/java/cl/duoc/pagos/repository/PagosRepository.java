package cl.duoc.pagos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.pagos.model.Pagos;

public interface PagosRepository extends JpaRepository<Pagos, Long> {
   /* Este metodo esta creado para buscar un pago asociado a un id de pedido
       especifico
    */
    Optional<Pagos> findByIdOrden(Long id);

    /*
    Con este metodo verificamos si un pedido ya fue procesado
    */
    boolean existsByIdOrden(Long id);
}
