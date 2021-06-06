package com.example.demo.service;

import com.example.demo.entities.Pedido;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PedidoService {

    Page<Pedido> findPaginated(int pageNo, int pageSize, int idrestaurante, String nombre, int inputEstadoMin, int inputEstadoMax, double inputPMin, double inputPMax, String fechainicio, String fechafin);
}
