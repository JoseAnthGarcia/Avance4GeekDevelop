package com.example.demo.service;

import com.example.demo.dtos.ExtraDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExtrasClienteService {

    Page<ExtraDTO> listaExtrasDisponiblesPaginada(int idRestaurante, int idPlato,String texto, int limitInfCa, int limitSupCa,
                                                  int limitInfPe, int limitSupPe, int disponible,Pageable pageable);
}
