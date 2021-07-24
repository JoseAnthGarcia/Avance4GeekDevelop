package com.example.demo.service;

import com.example.demo.dtos.ReporteDineroDTO;
import com.example.demo.dtos.ReportePedidoCDTO;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReporteDineroImpl implements ReporteDineroService{
    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<ReporteDineroDTO> findpage(int idCliente, int limitInf, int limitSup, String anio,String texto, String nombrec, Pageable pageable){
        return this.pedidoRepository.reportedinero(idCliente,limitInf,limitSup,anio,texto,nombrec,pageable);
    }
}
