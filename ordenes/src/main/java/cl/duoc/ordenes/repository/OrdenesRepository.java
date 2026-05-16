package cl.duoc.ordenes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.ordenes.model.Ordenes;

@Repository
public interface OrdenesRepository extends JpaRepository<Ordenes, Long> {
    //Metodo para listar las ordenes de un usuario especifico
    List<Ordenes> findByIdUsuario(Long idUsuario);

    /*Metodo para buscar ordenes por su estado
    List<Ordenes> findByEstadoOrden(String estadoOrden);
    */
    
}
