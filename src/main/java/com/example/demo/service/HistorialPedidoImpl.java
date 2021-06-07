package com.example.demo.service;

import com.example.demo.dtos.Pedido1DTO;
import com.example.demo.dtos.PedidoDTO;
import com.example.demo.dtos.PedidoValoracionDTO;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HistorialPedidoImpl implements HistorialPedidoService{

    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<PedidoValoracionDTO> findPaginated2(int idCliente, String texto, int limitInf, int limitSup, Pageable pageable) {
        return this.pedidoRepository.pedidosTotales2(idCliente,texto,limitInf,limitSup,pageable);
    }


}
