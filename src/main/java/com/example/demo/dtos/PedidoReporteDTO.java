package com.example.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PedidoReporteDTO {
    String getCodigo();
    String getFecha();
    int getCantidadplatos();
    BigDecimal getMontoplatos();
    BigDecimal getMontoextras();
    BigDecimal getDescuento();
    BigDecimal getPreciototal();
}
