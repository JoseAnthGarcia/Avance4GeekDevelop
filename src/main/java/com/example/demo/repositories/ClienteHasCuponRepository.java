package com.example.demo.repositories;

import com.example.demo.entities.Cliente_has_cupon;
import com.example.demo.entities.Cliente_has_cuponKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteHasCuponRepository extends JpaRepository<Cliente_has_cupon, Cliente_has_cuponKey> {
}
