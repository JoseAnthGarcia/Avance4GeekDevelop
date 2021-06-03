package com.example.demo.dtos;

import java.math.BigDecimal;

public interface PlatosDTO {

    int getIdplato();
    String getNombre();
    BigDecimal getPrecio();
    byte[] getFoto();
    String getFotocontenttype();
    String getFotonombre();

}
