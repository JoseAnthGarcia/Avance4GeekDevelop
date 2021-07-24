package com.example.demo.dtos;

import java.math.BigDecimal;

public interface UsuarioDtoReporteVentas {
    Integer getIdrestaurante();
    String getNombre();
    String getRuc();
    Integer getCantidadpedidos();
    BigDecimal getTotal();
    Integer getValoracion();


}
