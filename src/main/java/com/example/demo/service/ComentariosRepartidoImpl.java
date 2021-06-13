package com.example.demo.service;

import com.example.demo.entities.Pedido;
import com.example.demo.entities.Ubicacion;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
@Service
public class ComentariosRepartidoImpl implements  ComentarioRepartidorService {
    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<Pedido> comentariosRepartidor(int numeroPag, int tamPag,int idrepartidor){
        Pageable pageable = PageRequest.of(numeroPag - 1, tamPag);

        return pedidoRepository.comentariosRepartidor(idrepartidor, pageable);
    }

}
