package com.example.demo.dtos;

import java.math.BigDecimal;
// para evitar mapear la entidad y malograr la pagenitation
public interface ExtraDTO {

    String getNombre();
    int getIdextra();
    byte[] getFoto();
    String getFotocontenttype();
    String getFotonombre();
    BigDecimal getPreciounitario();
    String getTipo();
    int getDisponible();

}
