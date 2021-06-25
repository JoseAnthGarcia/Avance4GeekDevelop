package com.example.demo.dtos;

import java.time.LocalDate;

public interface PedidoValoracionDTO {
    String getNombre();
    byte[] getFoto();
    int getIdrestaurante();
    String getFecha();
    String getFechaPedido();
    String getHora();
    int getTiempoentrega();
    int getEstado();
    String getCodigo();
    int getIdcliente();
    Integer getValoracionrestaurante();
    String getComentariorestaurante();
    Integer getValoracionrepartidor();
    String getComentariorepartidor();
}
