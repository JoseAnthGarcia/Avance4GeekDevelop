package com.example.demo.service;

import com.example.demo.dtos.PlatoReporteDTO;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReportePlatoServiceImpl implements ReportePlatoService {

    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<PlatoReporteDTO> findPaginated(int pageNo, int pageSize, int idrestaurante, int estado, String nombre, String idcategoria) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.pedidoRepository.reportePlato(idrestaurante, estado, nombre, idcategoria, pageable);
    }
}
