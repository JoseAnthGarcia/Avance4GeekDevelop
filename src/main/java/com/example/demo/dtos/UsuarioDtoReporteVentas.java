package com.example.demo.dtos;

import java.math.BigDecimal;

public interface UsuarioDtoReporteVentas {

    String getNombre();
    String getRuc();
    Integer getEstado();
    BigDecimal getMismodistrito1();
    BigDecimal getMismodistrito0();
    Integer getValoracionrestaurante();
    Integer getCantpedidos();
    BigDecimal getMontototal();
}
