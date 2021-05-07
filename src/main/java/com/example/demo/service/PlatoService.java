package com.example.demo.service;

import com.example.demo.entities.Plato;
import org.springframework.data.domain.Page;

public interface PlatoService {

    Page<Plato> findPaginated(int pageNo, int pageSize);
}
