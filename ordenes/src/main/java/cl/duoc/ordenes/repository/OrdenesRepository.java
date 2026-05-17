package cl.duoc.ordenes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.ordenes.model.Ordenes;


public interface  OrdenesRepository extends JpaRepository<Ordenes, Long> {

    List<Ordenes> findByUsuarioId(Long usuarioId);

    List<Ordenes> findByEstadoOrden(String estadoOrden);
}
