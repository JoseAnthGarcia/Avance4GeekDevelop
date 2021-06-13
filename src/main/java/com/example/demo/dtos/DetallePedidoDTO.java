package com.example.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DetallePedidoDTO {
    String getCodigo();
    String getCliente();
    String getDireccion();
    LocalDate getFechapedido();
    String getCupon();
    BigDecimal getDescuento();
    int getEstado();
    int getMetodopago();
    String getComentario();
    BigDecimal getPreciototal();
    int getMismodistrito();
}
