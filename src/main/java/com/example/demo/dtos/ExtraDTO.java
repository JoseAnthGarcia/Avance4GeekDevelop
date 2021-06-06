package com.example.demo.dtos;

import java.math.BigDecimal;
// para evitar mapear la entidad y malograr la pagenitation
public interface ExtraDTO {

    int getIdextra();
    byte[] getFoto();
    String getFotocontenttype();
    String getFotonombre();
    String getNombre();
    BigDecimal getPreciounitario();
    int getDisponible();
    String getTipo();
}
