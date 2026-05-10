package cl.duoc.resena.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.resena.model.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProductoId(Long productoId);
    
    List<Resena> findByUsuarioId(Long usuarioId);
    
    List<Resena> findByCalificacion(int calificacion);
}
