package com.example.demo.service;

import com.example.demo.dtos.CuponClienteDTO;
import com.example.demo.dtos.PedidoValoracionDTO;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CuponClienteImpl implements CuponClienteService{


    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<CuponClienteDTO>  findPaginated2( String texto, int limitInf, int limitSup, Pageable pageable) {
        return this.pedidoRepository.listaCupones(texto,limitInf,limitSup,pageable);
    }
}
