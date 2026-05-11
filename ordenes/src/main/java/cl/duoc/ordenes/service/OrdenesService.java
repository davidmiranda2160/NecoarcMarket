package cl.duoc.ordenes.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.ordenes.dto.OrdenesRequest;
import cl.duoc.ordenes.dto.OrdenesResponse;
import cl.duoc.ordenes.mapper.OrdenesMapper;
import cl.duoc.ordenes.model.Ordenes;
import cl.duoc.ordenes.repository.OrdenesRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenesService {
    @Autowired
    private OrdenesRepository ordenesRepository;

    @Autowired
    private OrdenesMapper ordenesMapper;

    public OrdenesResponse crearOrden(OrdenesRequest request) {
        Ordenes orden = ordenesMapper.fromRequest(request);
        return ordenesMapper.toResponse(ordenesRepository.save(orden));
    }
    
    public List<OrdenesResponse> listarTodas() {
        return ordenesRepository.findAll().stream()
                .map(ordenesMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public OrdenesResponse obtenerPorId(Long id) {
        return ordenesRepository.findById(id)
                .map(ordenesMapper::toResponse)
                .orElse(null);
    }

    public OrdenesResponse actualizarOrden(Long id, OrdenesRequest request) {
        return ordenesRepository.findById(id).map(ordenExistente -> {
            ordenExistente.setIdUsuario(request.getIdUsuario());
            ordenExistente.setDireccionEnvio(request.getDireccionEnvio());
            return ordenesMapper.toResponse(ordenesRepository.save(ordenExistente));
        }).orElse(null);
    }

    public boolean eliminarOrden(Long id) {
        if (ordenesRepository.existsById(id)) {
            ordenesRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<OrdenesResponse> listarPorUsuario(Long idUsuario) {
        return ordenesRepository.findByIdUsuario(idUsuario).stream()
                .map(ordenesMapper::toResponse)
                .collect(Collectors.toList());
    }
}