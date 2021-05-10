package com.example.demo.service;

import com.example.demo.entities.Plato;
import com.example.demo.repositories.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PlatoServiceImpl implements PlatoService{

    @Autowired
    PlatoRepository platoRepository;

    @Override
    public Page<Plato> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.platoRepository.findByDisponible(true, pageable);
    }

    @Override
    public Page<Plato> findPaginated2(int pageNo, int pageSize, String nombre, boolean disponibilidad, double inputPMin, double inputPMax) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.platoRepository.findByDisponibleAndNombreIsContainingAndPrecioGreaterThanEqualAndPrecioLessThanEqual(disponibilidad, nombre, pageable, inputPMin, inputPMax);
    }


}
