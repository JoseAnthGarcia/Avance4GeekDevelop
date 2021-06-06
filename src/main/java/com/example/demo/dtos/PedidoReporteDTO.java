package com.example.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PedidoReporteDTO {
    String getCodigo();
    String getFecha();
    BigDecimal getMontoplatos();
    BigDecimal getMontoextras();
    BigDecimal getDescuento();
    BigDecimal getPreciototal();
}
