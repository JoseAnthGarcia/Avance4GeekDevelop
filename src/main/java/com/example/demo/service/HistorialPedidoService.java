package com.example.demo.service;

import com.example.demo.dtos.Pedido1DTO;
import com.example.demo.dtos.PedidoDTO;
import com.example.demo.dtos.PedidoValoracionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistorialPedidoService {
    Page<PedidoValoracionDTO> findPaginated2(int idCliente, String texto, Integer limitInf, Integer limitSup, Pageable pageable);
}
