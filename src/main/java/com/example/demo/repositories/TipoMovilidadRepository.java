package com.example.demo.repositories;

import com.example.demo.entities.TipoMovilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoMovilidadRepository extends JpaRepository<TipoMovilidad,Integer > {
}
