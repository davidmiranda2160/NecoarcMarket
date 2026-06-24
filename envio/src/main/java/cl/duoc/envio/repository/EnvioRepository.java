package cl.duoc.envio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.envio.model.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {

    Optional<Envio> findByOrdenId(Long ordenId);

    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);

    List<Envio> findByEstadoEnvio(String estadoEnvio);

    List<Envio> findByEmpresaTransporte(String empresaTransporte);

    List<Envio> findByUsuarioId(Long usuarioId);
}
