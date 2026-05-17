package cl.duoc.ordenes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.ordenes.model.Ordenes;
import java.util.List;


public interface  OrdenesRepository extends JpaRepository<Ordenes, Long> {

    //Metodo para buscar el historial de usuario
    List<Ordenes> findByUsuarioId(Long usuarioId);
}
