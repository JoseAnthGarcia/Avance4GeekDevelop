package com.example.demo.service;

import com.example.demo.entities.Pedido;
import com.example.demo.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Page<Pedido> findPaginated(int pageNo, int pageSize, int idrestaurante, String nombre, int inputEstadoMin, int inputEstadoMax, double inputPMin, double inputPMax) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.pedidoRepository.findByRestaurante_IdrestauranteAndCliente_NombresIsContainingAndEstadoGreaterThanEqualAndEstadoLessThanEqualAndPreciototalGreaterThanEqualAndPreciototalLessThanEqual(idrestaurante,
                nombre, inputEstadoMin, inputEstadoMax, inputPMin, inputPMax, pageable);
    }
}
