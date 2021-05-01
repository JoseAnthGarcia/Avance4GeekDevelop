package com.example.demo.repositories;

import com.example.demo.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRestRepository extends JpaRepository<Plato, Integer> {

}
