package com.example.demo.service;

import com.example.demo.dtos.PedidoValoracionDTO;
import com.example.demo.dtos.ReportePedido;
import com.example.demo.dtos.ReportePedidoCDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReporteTiempoService {
    Page<ReportePedidoCDTO> findPaginated3(int idCliente, int limitInf, int limitSup,String anio, String texto, int limitcant1,int limitcant2, Pageable pageable);
}
