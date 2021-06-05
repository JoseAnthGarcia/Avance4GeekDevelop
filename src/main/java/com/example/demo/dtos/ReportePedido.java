package com.example.demo.dtos;

import java.math.BigDecimal;

public interface ReportePedido {

    int getIdrestaurante();
    String getNombrerest();
    int getNumPedidos();
    int getMes();
    String getCodigo();
    BigDecimal getTotal();


}
