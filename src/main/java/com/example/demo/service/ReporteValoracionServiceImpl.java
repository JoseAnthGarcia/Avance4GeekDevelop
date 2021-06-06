package com.example.demo.service;

import com.example.demo.dtos.ValoracionReporteDTO;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReporteValoracionServiceImpl implements ReporteValoracionService{

    @Autowired
    PedidoRepository pedidoRepository;


    @Override
    public Page<ValoracionReporteDTO> findPaginated(int pageNo, int pageSize, int idrestaurante, int estado, String valoracion, String fechainicio, String fechafin) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.pedidoRepository.valoracionReporte(idrestaurante, estado, valoracion, fechainicio, fechafin, pageable);
    }
}
