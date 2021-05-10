package com.example.demo.repositories;

import com.example.demo.entities.Plato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Integer> {


    Page<Plato> findByDisponible(boolean disponible, Pageable pageable);

    Page<Plato> findByIdrestauranteAndIdcategoriaplatoAndDisponibleAndNombreIsContainingAndPrecioGreaterThanEqualAndPrecioLessThanEqual(int idrestaurante,int idcategoriaplato,boolean disponible, String nombre, Pageable pageable, double inputPMin, double inputPMax);

}
