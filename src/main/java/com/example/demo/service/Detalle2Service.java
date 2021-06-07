package com.example.demo.service;

import com.example.demo.dtos.PedidoValoracionDTO;
import com.example.demo.dtos.Plato_has_PedidoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Detalle2Service {
    Page<Plato_has_PedidoDTO> findPaginated2(String codigo, Pageable pageable);
}
