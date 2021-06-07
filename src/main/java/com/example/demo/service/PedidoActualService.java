package com.example.demo.service;

import com.example.demo.dtos.PedidoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PedidoActualService {

    Page<PedidoDTO> findPaginated(int idCliente, String texto, int limitInf, int limitSup,Pageable pageable);
}
