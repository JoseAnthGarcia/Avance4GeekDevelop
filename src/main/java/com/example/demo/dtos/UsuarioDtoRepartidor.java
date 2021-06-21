package com.example.demo.dtos;

import java.math.BigDecimal;

public interface UsuarioDtoRepartidor {
    String getNombres();
    String getApellidos();
    String getDni();
    String getCorreo();
    String getTelefono();
    Integer getValoracionrepartidor();
    Integer getCantpedidos();
    BigDecimal getMontototal();
}
