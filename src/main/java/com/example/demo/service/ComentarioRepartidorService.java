package com.example.demo.service;

import com.example.demo.entities.Pedido;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpSession;

public interface ComentarioRepartidorService {

    Page<Pedido> comentariosRepartidor(int numeroPag, int tamPag,int idrepartidor);
}
