package com.example.demo.dtos;

import java.time.LocalDate;

public interface PedidoDTO {
    String getNombre();
    byte[] getFoto();
    int getIdrestaurante();
    String getFechapedido();
    int getTiempoentrega();
    int getEstado();
    String getCodigo();
    int getIdcliente();
}
