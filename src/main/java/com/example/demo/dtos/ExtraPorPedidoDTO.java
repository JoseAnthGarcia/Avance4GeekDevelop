package com.example.demo.dtos;

import java.math.BigDecimal;

public interface ExtraPorPedidoDTO {
    String getNombre();
    BigDecimal getPreciounitario();
    int getCantidad();
    BigDecimal getPreciototal();
}
