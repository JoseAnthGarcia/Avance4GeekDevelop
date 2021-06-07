package com.example.demo.service;

import com.example.demo.dtos.PlatosDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlatoClienteService {
    Page<PlatosDTO> listaPlatoPaginada(int idRest, String texto, Integer limitInf, Integer limitSup, Pageable pageable);
}
