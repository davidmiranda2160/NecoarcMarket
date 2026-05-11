package cl.duoc.ordenes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.ordenes.model.Ordenes;

@Repository
public interface OrdenesRepository extends JpaRepository<Ordenes, Long> {
    //Metodo para listar las ordenes de un usuario especifico
    List<Ordenes> findByIdUsuario(Long idUsuario);

    //Metodo para buscar ordenes por su estado
    List<Ordenes> findByEstadoOrdenes(String estadoOrden);

    //Metodo para obtener la orden mas reciente de un usuario
    Optional<Ordenes> findByUsuarioFechaCreacion(Long idUsuario);

}
