package com.example.demo.service;

import com.example.demo.dtos.PedidoValoracionDTO;
import com.example.demo.dtos.ReportePedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportePedidoCService {
    Page<ReportePedido> findPaginated3(int idCliente, int limitInf, int limitSup, String texto, String numpedidos, Pageable pageable);

}
