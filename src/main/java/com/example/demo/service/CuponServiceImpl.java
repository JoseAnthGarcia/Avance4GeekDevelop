package com.example.demo.service;


import com.example.demo.entities.Cupon;
import com.example.demo.repositories.CuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CuponServiceImpl implements CuponService {

    @Autowired
    CuponRepository cuponRepository;

    @Override
    public Page<Cupon> findPaginated2(int pageNo, int pageSize, String nombre, Date caducidad, int inputDescuentoMin, int inputDescuentoMax) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.cuponRepository.findByNombreIsContainingAndFechafinAndDescuentoGreaterThanEqualAndDescuentoLessThanEqual(nombre, caducidad, inputDescuentoMin, inputDescuentoMax, pageable);
    }
}
