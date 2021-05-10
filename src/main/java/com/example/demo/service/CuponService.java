package com.example.demo.service;

import com.example.demo.entities.Cupon;
import org.springframework.data.domain.Page;

import java.util.Date;

public interface CuponService {

    Page<Cupon> findPaginated2(int pageNo, int pageSize, String nombre, Date caducidad, int inputDescuento);
}
