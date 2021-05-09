package com.example.demo.repositories;

import com.example.demo.entities.CategoriaExtra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaExtraRepository extends JpaRepository<CategoriaExtra,Integer> {
}
