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




}
