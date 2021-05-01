package com.example.demo.repositories;

import com.example.demo.entities.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuponRepository extends JpaRepository<Cupon,Integer> {
}
