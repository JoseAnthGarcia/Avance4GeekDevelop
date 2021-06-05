package com.example.demo.service;

import com.example.demo.entities.Pedido;
import com.example.demo.entities.Ubicacion;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import javax.servlet.http.HttpSession;


public interface PedidoService {

    Page<Pedido> findPaginated(int pageNo, int pageSize, int idrestaurante, String nombre, int inputEstadoMin, int inputEstadoMax, double inputPMin, double inputPMax);


}
