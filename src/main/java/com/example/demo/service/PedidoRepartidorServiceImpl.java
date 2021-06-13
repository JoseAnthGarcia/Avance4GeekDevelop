package com.example.demo.service;

import com.example.demo.entities.Pedido;
import com.example.demo.entities.Ubicacion;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class PedidoRepartidorServiceImpl implements  PedidoRepartidorService {

    @Autowired
    PedidoRepository pedidoRepository;

     @Override
    public Page<Pedido> pedidosPaginacion(int numeroPag, int tamPag, HttpSession session){
        Pageable pageable = PageRequest.of(numeroPag - 1, tamPag);
        Ubicacion ubicacionActual = (Ubicacion) session.getAttribute("ubicacionActual");

        return pedidoRepository.findByEstadoAndUbicacion_DistritoOrderByFechapedidoAsc(4, ubicacionActual.getDistrito(), pageable);
    }
    /*
    public Page<Pedido> listaPedidosReporte(int numeroPag, int tamPag, int estado, Usuario repartidor, String fechaMin, String fechaMax, double precioMin, double precioMax, int valMin, int valMax, String nombreRest){
        Pageable pageable = PageRequest.of(numeroPag - 1, tamPag);
        return  pedidoRepository.findByEstadoAndRepartidorAndFechapedidoBetweenAndPreciototalBetweenAndValoracionrepartidorBetweenAndAndRestaurante_NombreContaining(estado, repartidor,
                fechaMin, fechaMax, precioMin, precioMax, valMin, valMax, nombreRest, pageable);
    }*/
}
