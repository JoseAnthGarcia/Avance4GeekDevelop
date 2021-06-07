package com.example.demo.service;

import com.example.demo.dtos.RestauranteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestauranteClienteService {

    Page<RestauranteDTO> listaRestaurantePaginada(String texto, Integer limitInfP, Integer limitSupP, Integer limitInfVal, Integer limitSupVal, Integer limitIntCate, Integer limitSupCate,Integer iddistrito, Pageable pageable);

}