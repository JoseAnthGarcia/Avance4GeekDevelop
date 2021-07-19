package com.example.demo.service;

import com.example.demo.dtos.PlatosDTO;
import com.example.demo.repositories.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PlatoClienteServiceImpl implements PlatoClienteService{

    @Autowired
    PlatoRepository platoRepository;

    @Override
    public Page<PlatosDTO> listaPlatoPaginada(int idRest, String texto, Integer limitInf, Integer limitSup, Integer limitInfC, Integer limitSupC, Pageable pageable) {
        return this.platoRepository.listaPlato(idRest,texto,limitInf,limitSup,limitInfC,limitSupC,pageable);
    }
}
