package com.example.demo.service;

import com.example.demo.dtos.ReporteDineroDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReporteDineroService {
    Page<ReporteDineroDTO> findpage(int idcliente, int limit1mes, int limit2mes,String anio, String texto, String nombrec, Pageable pageable );
}
