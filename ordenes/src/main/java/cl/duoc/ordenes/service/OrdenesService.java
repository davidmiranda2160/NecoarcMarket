package cl.duoc.ordenes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.ordenes.mapper.OrdenesMapper;
import cl.duoc.ordenes.repository.OrdenesRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenesService {
    @Autowired
    private OrdenesRepository ordenesRepository;

    @Autowired
    private OrdenesMapper ordenesMapper;

}
