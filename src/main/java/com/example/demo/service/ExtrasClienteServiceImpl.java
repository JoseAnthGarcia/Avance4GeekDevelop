package com.example.demo.service;

import com.example.demo.dtos.ExtraDTO;
import com.example.demo.repositories.ExtraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ExtrasClienteServiceImpl implements ExtrasClienteService{

    @Autowired
    ExtraRepository extraRepository;

    @Override
    public Page<ExtraDTO> listaExtrasDisponiblesPaginada(int idRestaurante, int idPlato,String texto, int limitInfCa, int limitSupCa, int limitInfPe, int limitSupPe, int disponible,Pageable pageable) {
        return this.extraRepository.listaExtrasDisponibles(idRestaurante,idPlato,texto,limitInfCa,limitSupCa,limitInfPe,limitSupPe, disponible, pageable);
    }
}
