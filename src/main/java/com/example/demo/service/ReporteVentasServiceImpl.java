package com.example.demo.service;

import com.example.demo.dtos.PedidoReporteDTO;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReporteVentasServiceImpl implements ReporteVentasService{

    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<PedidoReporteDTO> findPaginated(int pageNo, int pageSize, int idrestaurante, int estado, String fechainicio, String fechafin, String codigo, double inputPrecioMin, double inputPrecioMax) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.pedidoRepository.pedidoReporte(idrestaurante, estado, fechainicio, fechafin, codigo, inputPrecioMin, inputPrecioMax, pageable);
    }
}
