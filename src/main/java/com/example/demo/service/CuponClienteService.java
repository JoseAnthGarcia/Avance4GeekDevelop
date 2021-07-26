package com.example.demo.service;

import com.example.demo.dtos.CuponClienteDTO;
import com.example.demo.dtos.PedidoValoracionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CuponClienteService {
    Page<CuponClienteDTO> findPaginated2(int idcliente,String texto, int limitInf, int limitSup, Pageable pageable);
}
