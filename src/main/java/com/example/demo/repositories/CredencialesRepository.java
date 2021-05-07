package com.example.demo.repositories;

import com.example.demo.entities.Credenciales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredencialesRepository  extends JpaRepository<Credenciales,Integer> {
}
