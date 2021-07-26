package com.example.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ReportePedido {
    int getIdrestaurante();
    String getNombrerest();
    int getNumPedidos();
    int getMes();
    String getCodigo();
    BigDecimal getTotal();
    LocalDate getFechaPedido();
    String getNombrecupon();
    BigDecimal getDescuento();
    int getTiempoEntrega();



}
