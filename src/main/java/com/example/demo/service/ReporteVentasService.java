package com.example.demo.service;

import com.example.demo.dtos.PedidoReporteDTO;
import org.springframework.data.domain.Page;

public interface ReporteVentasService {

    Page<PedidoReporteDTO> findPaginated(int pageNo, int pageSize, int idrestaurante, int estado, String fechainicio, String fechafin, String codigo, double inputPrecioMin, double inputPrecioMax);
}
