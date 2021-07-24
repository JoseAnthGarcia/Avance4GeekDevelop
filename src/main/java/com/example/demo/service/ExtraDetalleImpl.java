package com.example.demo.service;

import com.example.demo.dtos.ExtraPorPedidoDTO2;
import com.example.demo.dtos.Plato_has_PedidoDTO;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ExtraDetalleImpl implements ExtraDetalleService {
    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<ExtraPorPedidoDTO2> findPaginated2(String codigo, Pageable pageable) {
        return this.pedidoRepository.extrasPorPedido2(codigo, pageable);
    }
}
