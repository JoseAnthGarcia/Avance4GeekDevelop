package com.example.demo.service;

import com.example.demo.dtos.PedidoValoracionDTO;
import com.example.demo.dtos.Plato_has_PedidoDTO;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class Detalle2Impl implements  Detalle2Service{

    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<Plato_has_PedidoDTO> findPaginated2(String codigo,Pageable pageable) {
        return this.pedidoRepository.detalle2(codigo, pageable);
    }



}
