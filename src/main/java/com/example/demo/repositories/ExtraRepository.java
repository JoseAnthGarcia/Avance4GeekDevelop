package com.example.demo.repositories;

import com.example.demo.dtos.ExtraDTO;
import com.example.demo.entities.Extra;
import com.example.demo.entities.Plato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface ExtraRepository extends JpaRepository<Extra, Integer> {


    Page<Extra> findByIdrestauranteAndIdcategoriaextraAndDisponibleAndNombreIsContainingAndPreciounitarioGreaterThanEqualAndPreciounitarioLessThanEqual(int idrestaurante,int idcategoriaextra, boolean disponible, String nombre, Pageable pageable, double inputPMin, double inputPMax);


    Page<Extra> findByIdrestauranteAndDisponible(int idrestaurante,boolean disponible, Pageable pageable);



}
