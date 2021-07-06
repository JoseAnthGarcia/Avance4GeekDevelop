package com.example.demo.service;

import com.example.demo.dtos.RestauranteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestauranteClienteService {

    Page<RestauranteDTO> listaRestaurantePaginada(Integer limitInfP, Integer limitSupP,Integer limitInfVal, Integer limitSupVal, String texto, String id1,String id2, String id3,Integer iddistrito, Pageable pageable);

}