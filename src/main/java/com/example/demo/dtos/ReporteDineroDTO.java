package com.example.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ReporteDineroDTO {
    String getNombrerest();
    int getMes();
    LocalDate getFechapedido();
    String getNombrecupon();
    BigDecimal getDescuento();
}
