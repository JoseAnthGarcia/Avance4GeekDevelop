package com.example.demo.service;

import com.example.demo.dtos.RestauranteDTO;
import com.example.demo.repositories.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RestauranteClienteImpl2 implements RestauranteClienteService2{
    @Autowired
    RestauranteRepository restauranteRepository;

    @Override
    public Page<RestauranteDTO> listaRestaurantePaginada2(Integer limitInfP, Integer limitSupP, Integer limitInfVal, Integer limitSupVal, String texto, String id1, String id2, String id3, Integer iddistrito, Pageable pageable) {
        return restauranteRepository.listaRestaurante2(limitInfP,limitSupP,limitInfVal,limitSupVal,texto,id1,id2,id3,iddistrito, pageable);
    }
}
