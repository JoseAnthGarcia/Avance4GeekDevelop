package com.example.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CuponClienteDTO {
    int getIdcupon();
    String getNombrecupon();
    BigDecimal getDescuento();
    LocalDate getFechafin();
    String getPolitica();
    String getNombrerestaurante();
    Integer getUtilizado();
    String getNombrescliente();
    Integer getIdcliente();
}
