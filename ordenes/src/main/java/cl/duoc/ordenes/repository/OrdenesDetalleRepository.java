package cl.duoc.ordenes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.ordenes.model.OrdenesDetalle;

@Repository
public interface OrdenesDetalleRepository extends JpaRepository<OrdenesDetalle, Long > {

    List<OrdenesDetalle> findByOrdenId(Long ordenId);

}
