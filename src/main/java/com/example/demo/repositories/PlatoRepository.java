package com.example.demo.repositories;

import com.example.demo.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Integer> {

    List<Plato> findByDisponible(boolean disponible);

}
