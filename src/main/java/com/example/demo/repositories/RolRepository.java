package com.example.demo.repositories;

import com.example.demo.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Rol findByTipo(String tipo);
}
