package com.example.demo.service;

import com.example.demo.dtos.PedidoValoracionDTO;
import com.example.demo.dtos.ReportePedido;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReportePedidoCImpl implements ReportePedidoCService {
    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<ReportePedido> findPaginated3(int idCliente, int limitInf, int limitSup, String texto,String anio,int limit1cant,int limit2cant, Pageable pageable){
        return this.pedidoRepository.reportexmes(idCliente,limitInf,limitSup,texto,anio,limit1cant,limit2cant,pageable);
    }

}
