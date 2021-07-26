package com.example.demo.service;

import com.example.demo.dtos.ValoracionReporteDTO;
import org.springframework.data.domain.Page;

public interface ReporteValoracionService {
    Page<ValoracionReporteDTO> findPaginated(int pageNo, int pageSize, int idrestaurante, int estado, String valoracion, String fechainicio, String fechafin);
}
