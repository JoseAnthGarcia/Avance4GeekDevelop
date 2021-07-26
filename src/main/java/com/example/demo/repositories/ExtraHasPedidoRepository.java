package com.example.demo.repositories;

import com.example.demo.entities.Extra_has_pedido;
import com.example.demo.entities.Extra_has_pedidoKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraHasPedidoRepository extends JpaRepository<Extra_has_pedido, Extra_has_pedidoKey> {
}
