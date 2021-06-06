package com.example.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Pedido1DTO {
    String getCodigo();
    String getNombrerest();
    int getUtilizado();
    BigDecimal getPreciototal();
    int getMismodistrito();
    int getEstado();
    int getIdmetodopago();
    LocalDate getFechapedido();
    int getTiempoentrega();
    String getNombrecupon();
    int getDescuento();
}
