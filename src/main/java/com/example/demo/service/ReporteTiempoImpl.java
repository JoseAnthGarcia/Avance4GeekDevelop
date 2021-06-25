package com.example.demo.service;

import com.example.demo.dtos.ReportePedido;
import com.example.demo.dtos.ReportePedidoCDTO;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReporteTiempoImpl  implements  ReporteTiempoService{
    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<ReportePedidoCDTO> findPaginated3(int idCliente, int limitInf, int limitSup,String anio, String texto, int limitcant1,int limitcant2, Pageable pageable){
        return this.pedidoRepository.reportetiempo(idCliente,limitInf,limitSup,anio,texto,limitcant1,limitcant2,pageable);
    }
}
