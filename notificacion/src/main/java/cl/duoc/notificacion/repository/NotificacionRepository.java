package cl.duoc.notificacion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.notificacion.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    //Metodo de historial
    List<Notificacion> findByUsuarioIdOrderByFechaEnvioDesc(Long usuarioId);
}
