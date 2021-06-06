package com.example.demo.service;

import com.example.demo.entities.Cupon;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Date;

public interface CuponService {

    Page<Cupon> findPaginated2(int pageNo, int pageSize, int idrestaurante, String nombre, LocalDate fechainicio, LocalDate fechafin, int inputDescuentoMin, int inputDescuentoMax);
}
