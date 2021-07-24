package com.example.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DetallePedidoDTO {
    String getCodigo();
    String getCliente();
    String getDireccion();
    String getFechapedido();
    String getCupon();
    BigDecimal getDescuento();
    BigDecimal getCantidadapagar();
    int getEstado();
    String getMetodopago();
    String getComentario();
    BigDecimal getPreciototal();
    int getMismodistrito();
    String getTelc();
    String getTelr();
    String getRepartidor();
}
