package com.example.demo.service;

import com.example.demo.dtos.PedidoDTO;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class PedidoActualImpl implements PedidoActualService {

    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<PedidoDTO> findPaginated(int idCliente, String texto, Integer limitInf, Integer limitSup, Pageable pageable) {
        return this.pedidoRepository.pedidosTotales(idCliente,texto,limitInf,limitSup,pageable);
    }
}
