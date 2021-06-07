package com.example.demo.service;

import com.example.demo.entities.Pedido;
import com.example.demo.entities.Ubicacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

public interface PedidoRepartidorService {

    Page<Pedido> pedidosPaginacion(int numeroPag, int tamPag, HttpSession session);

}
