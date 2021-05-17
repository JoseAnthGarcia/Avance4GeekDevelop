package com.example.demo.service;

import com.example.demo.entities.Restaurante;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RestauranteService {
    @Autowired
    RestauranteRepository restauranteRepository;

    public Page<Restaurante> restaurantePaginacion(int numeroPag, int tamPag){
        Pageable pageable = PageRequest.of(numeroPag - 1, tamPag);
        return restauranteRepository.findByEstado(2, pageable);
    }
    public Page<Restaurante> restBusqueda(int numeroPag, int tamPag, String nombreRest, String ruc,int fechaRegistro){
        Pageable pageable = PageRequest.of(numeroPag - 1, tamPag);
        return restauranteRepository.buscarRest(nombreRest,ruc,fechaRegistro, pageable);
    }

}
