package com.example.demo.dtos;

import java.time.LocalDate;

public interface PedidoDTO {
    String getNombre();
    LocalDate getFechapedido();
    int getTiempoentrega();
    int getEstado();
    String getCodigo();

}
