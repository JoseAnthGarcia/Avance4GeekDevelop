package com.example.demo.service;

import com.example.demo.dtos.PlatoReporteDTO;
import org.springframework.data.domain.Page;

public interface ReportePlatoService {

    Page<PlatoReporteDTO> findPaginated(int pageNo, int pageSize, int idrestaurante, int estado, String nombre, String idcategoria);
}
