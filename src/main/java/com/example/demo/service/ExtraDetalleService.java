package com.example.demo.service;

import com.example.demo.dtos.ExtraPorPedidoDTO2;
import com.example.demo.dtos.Plato_has_PedidoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExtraDetalleService {
    Page<ExtraPorPedidoDTO2> findPaginated2(String codigo, Pageable pageable);
}
