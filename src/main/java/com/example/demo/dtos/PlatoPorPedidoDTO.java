package com.example.demo.dtos;

import java.math.BigDecimal;

public interface PlatoPorPedidoDTO {
    String getNombre();
    BigDecimal getPreciounitario();
    int getCantidad();
    BigDecimal getPreciototal();
    String getComentario();
}
