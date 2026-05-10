package cl.duoc.busqueda.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.busqueda.model.Busqueda;

@Repository
public interface BusquedaRepository extends JpaRepository<Busqueda, Long> {
    
    //Metodo para encontrar pedido por codigo de rastreo
    Optional<Busqueda> findByCodigoSeguimiento(String codigoSeguimiento);
}
