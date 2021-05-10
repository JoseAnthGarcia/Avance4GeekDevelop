package com.example.demo.service;

import com.example.demo.entities.Plato;
import org.springframework.data.domain.Page;

public interface PlatoService {

    Page<Plato> findPaginated(int pageNo, int pageSize);


    Page<Plato> findPaginated2(int pageNo, int pageSize, String nombre, boolean disponibilidad, double inputPMin, double inputPMax);

}
