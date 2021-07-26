package com.example.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Pedido1DTO {
    String getNombrecupon();
    String getCodigo();
    BigDecimal getCantp();
    String getNombrerest();
    BigDecimal getPreciototal();
    int getMismodistrito();
    int getEstado();
    int getIdmetodopago();
    LocalDate getFechapedido();
    int getTiempoentrega();
    int getDescuento();
    int getIdcliente();
    String getObsrest();

}
