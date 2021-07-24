package com.example.demo.dtos;

import java.math.BigDecimal;

public interface UsuarioDtoReporteIngresos {
   Integer getIdrestaurante();
    String  getNombre();
    String  getRuc();
   String getDistrito();
    Integer getEstado();

    Integer getCantidadpedidos();
    BigDecimal getIngresostotalesmismodistrito();
    BigDecimal getIngresostotalesdiferentedistrito();
   BigDecimal getIngresosTotales();


}
