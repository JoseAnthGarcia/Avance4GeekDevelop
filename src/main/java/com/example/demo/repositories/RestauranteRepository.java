package com.example.demo.repositories;

import com.example.demo.entities.Restaurante;
import com.example.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {
    @Query(value = "select * from restaurante where idadministrador = ?1", nativeQuery = true)
    Restaurante encontrarRest (int id);
}
